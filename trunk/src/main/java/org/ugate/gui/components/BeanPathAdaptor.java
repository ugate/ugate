package org.ugate.gui.components;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;

/**
 * A {@linkplain Property} extension that allows a <b><code>.</code></b>
 * separated field path to be traversed on a bean until the final field name is
 * found that will be bound to the property. For example, assume there is a
 * <code>Person</code> class that has a field for an <code>Address</code> class
 * which in turn has a field for a <code>city</code>.
 * <p>
 * If the <code>city</code> field needs to be bound to a JavaFX control
 * {@linkplain Property} for UI updates/viewing it can be accomplished in the
 * following manner:
 * </p>
 * <p>
 * <code>Person person = new Person();<br/> 
 * PathProperty<Person, String> pp = new PathProperty<Person, String>(person, "address.city", String.class);<br/> 
 * Bindings.bindBidirectional(pp, myTextField.textProperty());<br/> 
 * </code>
 * </p>
 * 
 * @param <B>
 *            the bean type
 */
public class BeanPathAdaptor<B> {

	private final Map<String, PathProperty<Object, ?>> pathPropertiesMap;
	private B bean;
	
	public BeanPathAdaptor(final B bean) {
		pathPropertiesMap = new TreeMap<>();
		setBean(bean);
	}
	
	/**
	 * Binds a {@linkplain Property} by traversing a supplied target object
	 * accessor's return object (or the accessor's return type class when the
	 * returned object is null in which case it will try to instantiate the
	 * class using a no-argument constructor) until it reaches the last field
	 * name in the expression that will be used for the final field's
	 * {@linkplain Property}
	 * 
	 * @param expString
	 *            the <b><code>.</code></b> separated field paths relative to
	 *            the {@linkplain #getBean()} that will be traversed
	 * @param fieldType
	 *            the field class type of the property
	 * 
	 * @throws NoSuchMethodException
	 *             thrown when a field accessor or setter cannot be found for any one of the fields in the path
	 */
	@SuppressWarnings("unchecked")
	public <T> void bind(final String expString, final Class<T> fieldType, final Property<T> property) 
					throws NoSuchMethodException {
		final String lPath = expString.toLowerCase();
		final String[] fieldNames = lPath.split("\\.");
		String path = null;
		PathProperty<Object, ?> pp = null;
		PathProperty<Object, T> ppt = null;
		Object target = getBean();
		MethodHandle a = null;
		MethodHandle s = null;
		for (int i = 0; i < fieldNames.length; i++) {
			path = lPath.substring(0, lPath.indexOf(fieldNames[i]) + fieldNames[i].length());
			if (getPathPropertiesMap().containsKey(path)) {
				if (i == (fieldNames.length - 1)) {
					ppt = (PathProperty<Object, T>) getPathPropertiesMap().get(path);
					ppt.setBean(target);
					if (property != null) {
						Bindings.bindBidirectional(ppt, property);
					}
				} else {
					pp = getPathPropertiesMap().get(path);
					pp.setBean(target);
					target = pp.get();
				}
			} else {
				try {
					a = buildAccessorWithLikelyPrefixes(target, path);
					s = buildSetter(a, target, fieldNames[i]);
					if (i == (fieldNames.length - 1)) {
						ppt = new PathProperty<>(target, fieldNames[i], fieldType, a, s,
								false);
						getPathPropertiesMap().put(path, ppt);
						if (property != null) {
							Bindings.bindBidirectional(ppt, property);
						}
					} else {
						pp = new PathProperty<>(target, fieldNames[i], Object.class, a, s,
								true);
						getPathPropertiesMap().put(path, pp);
						target = pp.get();
					}
				} catch (final Throwable t) {
					throw new RuntimeException(String.format(
							"Unable to instantiate expression %1$s on %2$s for %3$s", 
							target, path, expString), t);
				}
			}
		}
		updateMap(path);
	}
	
	protected void updateMap(final String excludePath) {
		if (getPathPropertiesMap().isEmpty()) {
			return;
		}
		final PathProperty<Object, ?> exPP = getPathPropertiesMap().get(excludePath);
		String[] fieldNames = null;
		String path, prevPath = null;
		for (final Map.Entry<String, PathProperty<Object, ?>> entry : getPathPropertiesMap().entrySet()) {
			if (excludePath == null || !excludePath.startsWith(entry.getKey())) {
				// TODO : update mapped path property values
			}
		}
	}
	
	/**
	 * Builds a setter {@linkplain MethodHandle}
	 * 
	 * @param accessor
	 *            the field's accesssor that will be used as the parameter type
	 *            for the setter
	 * @param target
	 *            the target object that the setter is for
	 * @param fieldName
	 *            the field name that the setter is for
	 * @return the setter {@linkplain MethodHandle}
	 */
	protected static MethodHandle buildSetter(final MethodHandle accessor, 
			final Object target, final String fieldName) {
		try {
			final MethodHandle mh1 = MethodHandles.lookup().findVirtual(target.getClass(), 
					buildMethodName("set", fieldName), 
					MethodType.methodType(void.class, 
							accessor.type().returnType())).bindTo(target);
			return mh1;
		} catch (final Throwable t) {
			throw new IllegalArgumentException("Unable to resolve setter "
					+ fieldName, t);
		}
	}

	/**
	 * Gets an accessor's return target value obtained by calling the accessor's
	 * {@linkplain MethodHandle#invoke(Object...)} method. When the value
	 * returned is <code>null</code> an attempt will be made to instantiate it
	 * using {@linkplain Class#newInstance()} on the accessor's
	 * {@linkplain MethodType#returnType()} method.
	 * 
	 * @param accessor
	 *            the accessor {@linkplain MethodHandle}
	 * @param fieldName
	 *            the accessor's field name
	 * @return the accessor's return target value
	 */
	protected static Object buildAccessorReturnTargetValue(final MethodHandle accessor) {
		Object targetValue = null;
		try {
			targetValue = accessor.invoke();
		} catch (final Throwable t) {
			targetValue = null;
		}
		if (targetValue == null) {
			try {
				targetValue = accessor.type().returnType().newInstance();
			} catch (final Exception e) {
				throw new IllegalArgumentException(
						String.format("Unable to get accessor return instance for %1$s using %2$s.", 
								accessor, accessor.type().returnType()));
			}
		}
		return targetValue;
	}

	/**
	 * Attempts to build a {@linkplain MethodHandle} accessor for the field
	 * name using common prefixes used for methods to access a field
	 * 
	 * @param target
	 *            the target object that the accessor is for
	 * @param fieldName
	 *            the field name that the accessor is for
	 * @return the accessor {@linkplain MethodHandle}
	 * @throws NoSuchMethodException
	 *             thrown when an accessor cannot be found for the field
	 */
	protected static MethodHandle buildAccessorWithLikelyPrefixes(final Object target, final String fieldName) 
					throws NoSuchMethodException {
		final MethodHandle mh = buildAccessor(target, fieldName, "get", "is", "has");
		if (mh == null) {
			throw new NoSuchMethodException(fieldName);
		}
		return mh;
	}

	/**
	 * Attempts to build a {@linkplain MethodHandle} accessor for the field
	 * name using common prefixes used for methods to access a field
	 * 
	 * @param target
	 *            the target object that the accessor is for
	 * @param fieldName
	 *            the field name that the accessor is for
	 * @return the accessor {@linkplain MethodHandle}
	 * @param fieldNamePrefix
	 *            the prefix of the method for the field name
	 * @return the accessor {@linkplain MethodHandle}
	 */
	protected static MethodHandle buildAccessor(final Object target, 
			final String fieldName, final String... fieldNamePrefix) {
		final String accessorName = buildMethodName(fieldNamePrefix[0], fieldName);
		try {
			return MethodHandles.lookup().findVirtual(target.getClass(), accessorName, 
					MethodType.methodType(
							target.getClass().getMethod(
									accessorName).getReturnType())).bindTo(target);
		} catch (final NoSuchMethodException e) {
			return fieldNamePrefix.length <= 1 ? null : 
				buildAccessor(target, fieldName, 
					Arrays.copyOfRange(fieldNamePrefix, 1, 
							fieldNamePrefix.length));
		} catch (final Throwable t) {
			throw new IllegalArgumentException(
					"Unable to resolve accessor " + accessorName, t);
		}
	}

	/**
	 * Builds a method name using a prefix and a field name
	 * 
	 * @param prefix
	 *            the method's prefix
	 * @param fieldName
	 *            the method's field name
	 * @return the method name
	 */
	protected static String buildMethodName(final String prefix, 
			final String fieldName) {
		return (fieldName.startsWith(prefix) ? fieldName : prefix + 
			fieldName.substring(0, 1).toUpperCase() + 
				fieldName.substring(1));
	}

	/**
	 * @return the cached {@linkplain Map} that contains the field path as the
	 *         keys and the {@linkplain PathProperty} and the values
	 */
	protected Map<String, PathProperty<Object, ?>> getPathPropertiesMap() {
		return pathPropertiesMap;
	}

	/**
	 * @return the root bean of the {@linkplain BeanPathAdaptor}
	 */
	public B getBean() {
		return bean;
	}
	
	/**
	 * Sets the root bean of the {@linkplain BeanPathAdaptor}. Any existing
	 * properties will be updated with the values relative to the paths within
	 * the bean.
	 * 
	 * @param bean
	 *            the bean to set
	 */
	public void setBean(final B bean) {
		if (bean == null) {
			throw new NullPointerException();
		}
		this.bean = bean;
		updateMap(null);
	}

	/**
	 * A {@linkplain Property} extension that allows a <b><code>.</code></b>
	 * separated field path to be traversed on a bean until the final field name is
	 * found that will be bound to the property. For example, assume there is a
	 * <code>Person</code> class that has a field for an <code>Address</code> class
	 * which in turn has a field for a <code>city</code>.
	 * <p>
	 * If the <code>city</code> field needs to be bound to a JavaFX control
	 * {@linkplain Property} for UI updates/viewing it can be accomplished in the
	 * following manner:
	 * </p>
	 * <p>
	 * <code>Person person = new Person();<br/> 
	 * PathProperty<Person, String> pp = new PathProperty<Person, String>(person, "address.city", String.class);<br/> 
	 * Bindings.bindBidirectional(pp, myTextField.textProperty());<br/> 
	 * </code>
	 * </p>
	 * 
	 * @param <BT>
	 *            the bean type
	 * @param <T>
	 *            the final field type found in the path for which the
	 *            {@linkplain Property} is for
	 */
	public class PathProperty<BT, T> extends ObjectPropertyBase<T> {

		private final boolean assumeValueFromAccessor;
		private final String fieldName;
		private final MethodHandle accessor;
		private final MethodHandle setter;
		private final Class<T> fieldType;
		private BT bean;

		/**
		 * Constructor
		 * 
		 * @param bean
		 *            the bean that the path belongs to
		 * @param fieldPath
		 *            the path to the <b><code>.</code></b> separated fields
		 *            that will be traversed on the bean until the final field
		 *            is found (which the property is bound to)
		 * @param fieldType
		 *            the {@linkplain Class} that the final field is
		 * @param assumeValueFromAccessor
		 *            true when the a value should be set or instantiated using
		 *            the {@linkplain #getAccessor()} return type
		 */
		protected PathProperty(final BT bean, final String fieldName, final Class<T> fieldType, final MethodHandle accessor, 
				final MethodHandle setter, final boolean assumeValueFromAccessor) {
			super();
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.accessor = accessor;
			this.setter = setter;
			this.assumeValueFromAccessor = assumeValueFromAccessor;
			setBean(bean);
		}
		
		/**
		 * Gets an accessor's return target value obtained by calling the
		 * accessor's {@linkplain MethodHandle#invoke(Object...)} method. When
		 * the value returned is <code>null</code> an attempt will be made to
		 * instantiate it using {@linkplain Class#newInstance()} on the
		 * accessor's {@linkplain MethodType#returnType()} method.
		 * 
		 * @return the accessor's return target value
		 */
		@SuppressWarnings("unchecked")
		private T buildAccessorReturnTargetValue() {
			T targetValue = null;
			try {
				targetValue = (T) getAccessor().invoke();
			} catch (final Throwable t) {
				targetValue = null;
			}
			if (targetValue == null) {
				try {
					targetValue = (T) getAccessor().type().returnType().newInstance();
				} catch (final Exception e) {
					throw new IllegalArgumentException(
							String.format("Unable to get accessor return instance for %1$s using %2$s.", 
									getAccessor(), getAccessor().type().returnType()));
				}
			}
			return targetValue;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final T v) {
			try {
//				final MethodHandle mh2 = MethodHandles.insertArguments(getSetter(), 0, value);
				getSetter().invoke(v);
				super.set(v);
			} catch (final Throwable t) {
				throw new RuntimeException("Unable to set value: " + v, t);
			}
		};

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get() {
			try {
				final T mprop = (T) getAccessor().invoke();
				if (!super.get().equals(mprop)) {
					super.set(mprop);
				}
				return super.get();
			} catch (final Throwable t) {
				throw new RuntimeException("Unable to get value", t);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BT getBean() {
			return bean;
		}
		
		/**
		 * Sets a new bean for the path property
		 * 
		 * @param bean
		 *            the bean to set
		 */
		public void setBean(final BT bean) {
			if (getBean().equals(bean)) {
				return;
			}
			getAccessor().bindTo(bean);
			getSetter().bindTo(bean);
			if (isAssumeValueFromAccessor()) {
				set(buildAccessorReturnTargetValue());
			}
			this.bean = bean;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return fieldName;
		}

		/**
		 * @return the name of the field for the
		 *         {@linkplain PropertyMethodHandles}
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * @return the getter
		 */
		protected MethodHandle getAccessor() {
			return accessor;
		}

		/**
		 * @return the setter
		 */
		protected MethodHandle getSetter() {
			return setter;
		}

		/**
		 * @return true when the a value should be set or instantiated using the
		 *         {@linkplain #getAccessor()} return type
		 */
		public boolean isAssumeValueFromAccessor() {
			return assumeValueFromAccessor;
		}

		/**
		 * @return the field type of the property value
		 */
		public Class<T> getFieldType() {
			return fieldType;
		}
	}
}
