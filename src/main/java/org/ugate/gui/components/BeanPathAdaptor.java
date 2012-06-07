package org.ugate.gui.components;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

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
 * @param <T>
 *            the final field type found in the path for which the
 *            {@linkplain Property} is for
 */
public class BeanPathAdaptor {

	private static final ObservableMap<String, PathProperty<?, ?>> PPS = FXCollections.observableHashMap();
	
	/**
	 * Builds a {@linkplain PropertyMethodHandles} by traversing a supplied
	 * target object accessor's return object (or the accessor's return type
	 * class when the returned object is null in which case it will try to
	 * instantiate the class using a no-argument constructor) until it
	 * reaches the last field name in the expression that will be used for
	 * the final field's {@linkplain PropertyMethodHandles}
	 * 
	 * @param initialTarget
	 *            the initial target object that will be traversed
	 * @param expString
	 *            the <b><code>.</code></b> separated fields that will be
	 *            traversed
	 * @return the {@linkplain PropertyMethodHandles} for the last field in
	 *         the expression string
	 * @throws NoSuchMethodException
	 *             thrown when a field accessor or setter cannot be found
	 */
	public <T> void bind(final Object initialTarget, 
			final String expString, final Class<T> fieldType, final Property<T> property) throws NoSuchMethodException {
		final String[] fieldNames = expString.split("\\.");
		String path = null;
		PathProperty pp = null;
		Object target = initialTarget;
		MethodHandle a = null;
		MethodHandle s = null;
		for (int i = 0; i < fieldNames.length; i++) {
			path = expString.substring(0, expString.indexOf(fieldNames[i]) + fieldNames[i].length());
			if (PPS.containsKey(path)) {
				// TODO : update the path properties with new values contained in target path
			}
			try {
				a = buildAccessorWithLikelyPrefixes(target, path);
				s = buildSetter(a, target, fieldNames[i], i < (fieldNames.length - 1));
				pp = new PathProperty(target, fieldNames[i], null, a, s);
				PPS.put(path, pp);
				target = pp.getBean();
			} catch (final Throwable t) {
				throw new RuntimeException(String.format(
						"Unable to instantiate expression %1$s on %2$s for %3$s", 
						target, path, expString), t);
			}
		}
	}
	
	/**
	 * Builds a setter {@linkplain MethodHandle}
	 * 
	 * @param accessor
	 *            the field's accesssor that will be used to set the
	 *            setter's argument when the insert setter argument is set
	 *            to true
	 * @param target
	 *            the target object that the setter is for
	 * @param fieldName
	 *            the field name that the setter is for
	 * @param insertSetterArgument
	 *            true to insert the setter argument on the
	 *            {@linkplain MethodHandle}
	 * @return the setter {@linkplain MethodHandle}
	 */
	protected static MethodHandle buildSetter(final MethodHandle accessor, 
			final Object target, final String fieldName, 
			final boolean insertSetterArgument) {
		Object sa = null;
		if (insertSetterArgument) {
			try {
				sa = accessor.invoke();
			} catch (final Throwable t) {
				sa = null;
			}
			if (sa == null) {
				try {
					sa = accessor.type().returnType().newInstance();
				} catch (final Exception e) {
					throw new IllegalArgumentException(
							String.format("Unable to build setter expression for %1$s using %2$s.", 
									fieldName, accessor.type().returnType()));
				}
			}
		}
		try {
			final MethodHandle mh1 = MethodHandles.lookup().findVirtual(target.getClass(), 
					buildMethodName("set", fieldName), 
					MethodType.methodType(void.class, 
							accessor.type().returnType())).bindTo(target);
			if (sa != null) {
//				final MethodHandle mh2 = MethodHandles.insertArguments(mh1, 0, sa);
				mh1.invoke(sa);
			}
			return mh1;
		} catch (final Throwable t) {
			throw new IllegalArgumentException("Unable to resolve setter "
					+ fieldName, t);
		}
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
	 * @param <T>
	 *            the final field type found in the path for which the
	 *            {@linkplain Property} is for
	 */
	public class PathProperty<B, T> extends ObjectPropertyBase<T> {

		private final String fieldName;
		private final MethodHandle accessor;
		private final MethodHandle setter;
		private final Class<T> type;
		private final B bean;

		/**
		 * Constructor
		 * 
		 * @param bean
		 *            the bean that the path belongs to
		 * @param fieldPath
		 *            the path to the <b><code>.</code></b> separated fields that
		 *            will be traversed on the bean until the final field is found
		 *            (which the property is bound to)
		 * @param type
		 *            the {@linkplain Class} that the final field is
		 */
		protected PathProperty(final B bean, final String fieldName, final Class<T> type, final MethodHandle accessor, 
				final MethodHandle setter) {
			super();
			this.bean = bean;
			this.fieldName = fieldName;
			this.type = type;
			this.accessor = accessor;
			this.setter = setter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final T v) {
			try {
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
		public B getBean() {
			return bean;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return fieldName;
		}
		
		/**
		 * @return the field type of the final path element
		 */
		public Class<T> getFieldType() {
			return type;
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
	}
}
