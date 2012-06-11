package org.ugate.gui.components;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	private FieldBean<Void, B> root;
	
	public BeanPathAdaptor(final B bean) {
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
	 * @param declaredFieldType
	 *            the field class type of the property
	 * 
	 * @throws NoSuchMethodException
	 *             thrown when a field accessor or setter cannot be found for any one of the fields in the path
	 */
	public <T> void bindBidirectional(final String expString, final Property<T> property) {
		getRoot().bidirectionalBindOperation(expString, property, false);
	}
	
	public <T> void unBindBidirectional(final String expString, final Property<T> property) {
		getRoot().bidirectionalBindOperation(expString, property, true);
	}

	/**
	 * @return the bean of the {@linkplain BeanPathAdaptor}
	 */
	public B getBean() {
		return getRoot().getBean();
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
		if (getRoot() == null) {
			this.root = new FieldBean<>(null, bean, null);
		} else {
			getRoot().setBean(bean);
		}
	}

	/**
	 * @return the root/top level {@linkplain FieldBean}
	 */
	protected final FieldBean<Void, B> getRoot() {
		return this.root;
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
	protected static class FieldBean<PT, BT> implements Serializable {

		private static final long serialVersionUID = 7397535724568852021L;
		private final Map<String, FieldBean<BT, ?>> fieldBeans = new HashMap<>();
		private final Map<String, FieldProperty<BT, ?>> fieldProperties = new HashMap<>();
		private FieldHandle<PT, BT> fieldHandle;
		private final FieldBean<?, PT> parent;
		private BT bean;

		/**
		 * Creates a {@linkplain FieldBean}
		 * @param parent
		 * @param fieldHandle
		 */
		protected FieldBean(final FieldBean<?, PT> parent, final FieldHandle<PT, BT> fieldHandle) {
			this.parent = parent;
			this.fieldHandle = fieldHandle;
			this.bean = this.fieldHandle.setDerivedValueFromAccessor();
			if (getParent() != null) {
				getParent().addFieldBean(this);
			}
		}

		/**
		 * Creates a {@linkplain FieldBean} with a generated
		 * {@linkplain FieldHandle} that targets the supplied bean and is
		 * projected on the parent {@linkplain FieldBean}. It assumes that the
		 * supplied {@linkplain FieldBean} has been set on the parent
		 * {@linkplain FieldBean}.
		 * 
		 * @see #createFieldHandle(Object, Object, String)
		 * @param parent
		 *            the parent {@linkplain FieldBean} (null when it's the root)
		 * @param bean
		 *            the bean that the {@linkplain FieldBean} is for
		 * @param fieldName
		 *            the field name of the parent {@linkplain FieldBean} for
		 *            which the new {@linkplain FieldBean} is for
		 */
		protected FieldBean(final FieldBean<?, PT> parent, final BT bean, final String fieldName) {
			if (bean == null) {
				throw new NullPointerException("Bean cannot be null");
			}
			this.parent = parent;
			this.bean = bean;
			this.fieldHandle = getParent() != null ? createFieldHandle(getParent().getBean(), 
					bean, fieldName) : null;
			if (getParent() != null) {
				getParent().addFieldBean(this);
			}
		}

		/**
		 * Generates a {@linkplain FieldHandle} that targets the supplied bean
		 * and is projected on the parent {@linkplain FieldBean} that has
		 * 
		 * @param parentBean
		 *            the parent bean
		 * @param bean
		 *            the child bean
		 * @param fieldName
		 *            the field name of the child within the parent
		 * @return the {@linkplain FieldHandle}
		 */
		@SuppressWarnings("unchecked")
		protected FieldHandle<PT, BT> createFieldHandle(final PT parentBean, final BT bean, 
				final String fieldName) {
			return new FieldHandle<PT, BT>(
					parentBean, fieldName, (Class<BT>) getBean().getClass());
		}

		/**
		 * Adds a child {@linkplain FieldBean} if it doesn't already exist.
		 * NOTE: It does <b>NOT</b> ensure the child bean has been set on the
		 * parent.
		 * 
		 * @param fieldBean
		 *            the {@linkplain FieldBean} to add
		 */
		protected void addFieldBean(final FieldBean<BT, ?> fieldBean) {
			if (!getFieldBeans().containsKey(fieldBean.getFieldName())) {
				getFieldBeans().put(fieldBean.getFieldName(), fieldBean);
			}
		}

		/**
		 * Adds or updates a child {@linkplain FieldProperty}. When the child
		 * already exists it will {@linkplain FieldProperty#setTarget(Object)}
		 * using the bean of the {@linkplain FieldProperty}.
		 * 
		 * @param fieldProperty
		 *            the {@linkplain FieldProperty} to add or update
		 */
		protected void addOrUpdateFieldProperty(final FieldProperty<BT, ?> fieldProperty) {
			if (getFieldProperties().containsKey(fieldProperty.getName())) {
				getFieldProperties().get(fieldProperty.getName()).setTarget(fieldProperty.getBean());
			} else {
				getFieldProperties().put(fieldProperty.getName(), fieldProperty);
			}
		}

		/**
		 * @see #setParentBean(Object)
		 * @return the bean that the {@linkplain FieldBean} represents
		 */
		public BT getBean() {
			return bean;
		}
		
		/**
		 * Sets the bean of the {@linkplain FieldBean} and it's underlying
		 * {@linkplain #getFieldNodes()} and {@linkplain #getFieldProperties()}
		 * 
		 * @see #setParentBean(Object)
		 * @param bean
		 */
		public void setBean(final BT bean) {
			if (bean == null) {
				throw new NullPointerException("Bean cannot be null");
			}
			this.bean = bean;
			for (final Map.Entry<String, FieldBean<BT, ?>> fn : getFieldBeans().entrySet()) {
				fn.getValue().setParentBean(getBean());
			}
			for (final Map.Entry<String, FieldProperty<BT, ?>> fp : getFieldProperties().entrySet()) {
				fp.getValue().setTarget(getBean());
			}
		}
		
		/**
		 * Binds a parent bean to the {@linkplain FieldBean} and it's underlying
		 * {@linkplain #getFieldNodes()} and {@linkplain #getFieldProperties()}
		 * 
		 * @see #setBean(Object)
		 * @param bean
		 *            the parent bean to bind to
		 */
		public void setParentBean(final PT bean) {
			if (bean == null) {
				throw new NullPointerException("Cannot bind to a null bean");
			} else if (fieldHandle == null) {
				throw new IllegalStateException("Cannot bind to a root " + 
						FieldBean.class.getSimpleName());
			}
			fieldHandle.setTarget(bean);
			setBean(fieldHandle.deriveValueFromAccessor());
		}

		/**
		 * Binds/Unbinds a {@linkplain FieldProperty} based upon the supplied
		 * <b><code>.</code></b> separated path to the field by traversing the
		 * matching children of the {@linkplain FieldBean} until the
		 * corresponding {@linkplain FieldProperty} is found (target bean uses
		 * the POJO from {@linkplain FieldBean#getBean()}). If the operation is
		 * bind and the {@linkplain FieldProperty} doesn't exist all relative
		 * {@linkplain FieldBean}s in the path will be instantiated using a
		 * no-argument constructor until the {@linkplain FieldProperty} is
		 * created and bound to the supplied {@linkplain Property}.
		 * 
		 * @see Bindings#bindBidirectional(Property, Property)
		 * @see Bindings#unbindBidirectional(Property, Property)
		 * @param fieldPath
		 *            the <code>.</code> separated field names
		 * @param property
		 *            the {@linkplain Property} to bind/unbind
		 * @param unbind
		 *            true to unbind, false to bind
		 */
		@SuppressWarnings("unchecked")
		public <T> void bidirectionalBindOperation(final String fieldPath, 
				final Property<T> property, final boolean unbind) {
			final String[] fieldNames = fieldPath.split("\\.");
			final boolean isProperty = fieldNames.length == 1;
			if (isProperty && getFieldProperties().containsKey(fieldNames[0])) {
				if (unbind) {
					Bindings.unbindBidirectional((Property<T>) 
							getFieldProperties().get(fieldNames[0]), property);
				} else {
					Bindings.bindBidirectional((Property<T>) 
							getFieldProperties().get(fieldNames[0]), property);
				}
			} else if (!isProperty && getFieldBeans().containsKey(fieldNames[0])) {
				// progress to the next child field/bean in the path chain
				final String nextFieldPath = fieldPath.substring(
						fieldPath.indexOf(fieldNames[1]));
				getFieldBeans().get(fieldNames[0]).bidirectionalBindOperation(
						nextFieldPath, property, unbind);
			} else if (!unbind) {
				// add a new bean/property chain
				if (isProperty) {
					final FieldProperty<BT, ?> childProp = new FieldProperty<>(
							getBean(), fieldNames[0], Object.class);
					addOrUpdateFieldProperty(childProp);
					bidirectionalBindOperation(fieldNames[0], property, unbind);
				} else {
					// create a handle to set the bean as a child of the current bean
					// if the child bean exists on the bean it will remain unchanged
					final FieldHandle<BT, Object> pfh = new FieldHandle<>(getBean(), 
							fieldNames[0], Object.class);
					final FieldBean<BT, ?> childBean = new FieldBean<>(this, pfh);
					// progress to the next child field/bean in the path chain
					final String nextFieldPath = fieldPath.substring(
							fieldPath.indexOf(fieldNames[1]));
					childBean.bidirectionalBindOperation(nextFieldPath, property, unbind);
				}
			}
		}

		/**
		 * @return the field name that the {@linkplain FieldBean} represents in
		 *         it's parent (null when the {@linkplain FieldBean} is root)
		 */
		public String getFieldName() {
			return fieldHandle != null ? fieldHandle.getFieldName() : null;
		}
		
		/**
		 * Determines if the {@linkplain FieldBean} contains a field with the
		 * specified name
		 * 
		 * @param fieldName
		 *            the field name to check for
		 * @return true when the field exists
		 */
		public boolean hasField(final String fieldName) {
			return getFieldBeans().containsKey(fieldName) || 
					getFieldProperties().containsKey(fieldName);
		}

		/**
		 * @return the parent {@linkplain FieldBean} (null when the
		 *         {@linkplain FieldBean} is root)
		 */
		public FieldBean<?, PT> getParent() {
			return parent;
		}

		/**
		 * @see #getFieldProperties()
		 * @return the {@linkplain Map} of fields that belong to the
		 *         {@linkplain FieldBean} that are not a
		 *         {@linkplain FieldProperty}, but rather exist as a
		 *         {@linkplain FieldBean} that may or may not contain their own
		 *         {@linkplain FieldProperty} instances
		 */
		protected Map<String, FieldBean<BT, ?>> getFieldBeans() {
			return fieldBeans;
		}

		/**
		 * @see #getFieldBeans()
		 * @return the {@linkplain Map} of fields that belong to the
		 *         {@linkplain FieldBean} that are not {@linkplain FieldBean}s,
		 *         but rather exist as a {@linkplain FieldProperty}
		 */
		protected Map<String, FieldProperty<BT, ?>> getFieldProperties() {
			return fieldProperties;
		}
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
	protected static class FieldProperty<BT, T> extends ObjectPropertyBase<T> {
		
		private final FieldHandle<BT, T> fieldHandle;

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
		 * @param setDerivedValue
		 *            true when the a value should be set or instantiated using
		 *            the {@linkplain #getAccessor()} return value or a new instance of it's return type
		 */
		protected FieldProperty(final BT bean, final String fieldName, final Class<T> fieldType) {
			super();
			this.fieldHandle = new FieldHandle<BT, T>(bean, fieldName, fieldType);
			set(fieldHandle.deriveValueFromAccessor());
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void set(final T v) {
			try {
				//final MethodHandle mh2 = MethodHandles.insertArguments(
				//fieldHandle.getSetter(), 0, v);
				T val;
				final boolean isStringType = fieldHandle.getFieldType().equals(
						String.class);
				if (v == null || (!isStringType && v.toString().isEmpty())) {
					val = (T) FieldHandle.defaultValue(fieldHandle
							.getFieldType());
				} else if (isStringType
						|| (v != null && v.getClass().isAssignableFrom(
								fieldHandle.getFieldType()))) {
					val = (T) fieldHandle.getFieldType().cast(v);
				} else {
					val = (T) FieldHandle.valueOf(get() != null ? get()
							.getClass() : fieldHandle.getFieldType(), v
							.toString());
				}
				super.set(val);
				fieldHandle.getSetter().invoke(val);
			} catch (final Throwable t) {
				throw new IllegalArgumentException("Unable to set value: " + v, t);
			}
		};

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get() {
			try {
				// TODO : here we are lazily loading the property which will prevent any property listeners
				// from receiving notice of a direct model field change until the next time the get method
				// is called here
				final T mprop = (T) fieldHandle.getAccessor().invoke();
				if (super.get() == null || !super.get().equals(mprop)) {
					super.set(mprop);
				}
				return super.get();
			} catch (final Throwable t) {
				throw new RuntimeException("Unable to get value", t);
			}
		}

		/**
		 * Binds a new target to the {@linkplain FieldHandle}
		 * 
		 * @param target
		 *            the target to bind to
		 */
		public void setTarget(final BT bean) {
			fieldHandle.setTarget(bean);
			set(fieldHandle.deriveValueFromAccessor());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BT getBean() {
			return fieldHandle.getTarget();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return fieldHandle.getFieldName();
		}
	}
	
	protected static class FieldHandle<T, F> {

		private static final Map<Class<?>, MethodHandle> valueOfMap = new HashMap<>(1);
		private static final Map<Class<?>, Object> NOBOX = new HashMap<>();
		static {
			NOBOX.put(Boolean.class, Boolean.FALSE);
			NOBOX.put(boolean.class, false);
			NOBOX.put(Byte.class, Byte.valueOf("0"));
			NOBOX.put(byte.class, Byte.valueOf("0").byteValue());
			NOBOX.put(Number.class, 0L);
			NOBOX.put(Short.class, Short.valueOf("0"));
			NOBOX.put(short.class, Short.valueOf("0").shortValue());
			NOBOX.put(Character.class, Character.valueOf(' '));
			NOBOX.put(char.class, ' ');
			NOBOX.put(Integer.class, Integer.valueOf(0));
			NOBOX.put(int.class, 0);
			NOBOX.put(Long.class, Long.valueOf(0));
		    NOBOX.put(long.class, 0L);
		    NOBOX.put(Float.class, Float.valueOf(0F));
		    NOBOX.put(float.class, 0F);
		    NOBOX.put(Double.class, Double.valueOf(0D));
		    NOBOX.put(double.class, 0D);
		    NOBOX.put(BigInteger.class, BigInteger.valueOf(0L));
		    NOBOX.put(BigDecimal.class, BigDecimal.valueOf(0D));
		}
		private final String fieldName;
		private MethodHandle accessor;
		private MethodHandle setter;
		private final Class<F> declaredFieldType;
		private T target;

		protected FieldHandle(final T target, final String fieldName, 
				final Class<F> declaredFieldType) {
			super();
			this.fieldName = fieldName;
			this.declaredFieldType = declaredFieldType;
			this.target = target;
			this.accessor = buildAccessorWithLikelyPrefixes(getTarget(), getFieldName());
			this.setter = buildSetter(getAccessor(), getTarget(), getFieldName());
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
		protected static MethodHandle buildAccessorWithLikelyPrefixes(final Object target, 
				final String fieldName) {
			final MethodHandle mh = buildAccessor(target, fieldName, "get", "is", "has");
			if (mh == null) {
				//throw new NoSuchMethodException(fieldName + " on " + target);
				throw new IllegalArgumentException(fieldName + " on " + target);
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
		 * Puts a <code>valueOf</code> {@linkplain MethodHandle} value using the
		 * target class as a key
		 * 
		 * @param target
		 *            the target object that the <code>valueOf</code> is for
		 */
		protected static void putValueOf(final Class<?> target) {
			if (valueOfMap.containsKey(target)) {
				return;
			}
			try {
				final MethodHandle mh1 = MethodHandles.lookup().findStatic(
						target, "valueOf",
						MethodType.methodType(target, String.class));
				valueOfMap.put(target, mh1);
			} catch (final Throwable t) {
				// class doesn't support it- do nothing
			}
		}

		/**
		 * Attempts to invoke a <code>valueOf</code> using the
		 * {@linkplain #getDeclaredFieldType()} class
		 * 
		 * @param value
		 *            the value to invoke the <code>valueOf</code> method on
		 * @return the result (null if the operation fails)
		 */
		public F valueOf(final String value) {
			return valueOf(getDeclaredFieldType(), value);
		}
		
		/**
		 * Attempts to invoke a <code>valueOf</code> using the
		 * specified class
		 * 
		 * @param valueOfClass
		 *            the class to attempt to invoke a <code>valueOf</code>
		 *            method on
		 * @param value
		 *            the value to invoke the <code>valueOf</code> method on
		 * @return the result (null if the operation fails)
		 */
		public static <VT> VT valueOf(final Class<VT> valueOfClass, 
				final Object value) {
			if (!valueOfMap.containsKey(valueOfClass)) {
				putValueOf(valueOfClass);
			}
			if (valueOfMap.containsKey(valueOfClass)) {
				try {
					return (VT) valueOfMap.get(valueOfClass).invoke(value);
				} catch (final Throwable t) {
					throw new IllegalArgumentException(String.format(
							"Unable to invoke valueOf on %1$s using %2$s", 
							value, valueOfClass), t);
				}
			}
			return null;
		}

		/**
		 * Gets a default value for the {@linkplain #getDeclaredFieldType()}
		 * 
		 * @return the default value
		 */
		public F defaultValue() {
			return defaultValue(getDeclaredFieldType());
		}

		/**
		 * Gets a default value for the specified class
		 * 
		 * @param clazz
		 *            the class
		 * @return the default value
		 */
		@SuppressWarnings("unchecked")
		public static <VT> VT defaultValue(final Class<VT> clazz) {
			return (VT) (NOBOX.containsKey(clazz) ? NOBOX.get(clazz) : null);
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
		 * Sets the derived value from {@linkplain #deriveValueFromAccessor()}
		 * using {@linkplain #getSetter()}
		 * 
		 * @see #deriveValueFromAccessor()
		 * @return the accessor's return target value
		 */
		public F setDerivedValueFromAccessor() {
			F derived = null;
			try {
				derived = deriveValueFromAccessor();
				getSetter().invoke(derived);
			} catch (final Throwable t) {
				throw new RuntimeException(String.format(
						"Unable to set %1$s on %2$s", derived, 
						getTarget()), t);
			}
			return derived;
		}
		
		/**
		 * Gets an accessor's return target value obtained by calling the
		 * accessor's {@linkplain MethodHandle#invoke(Object...)} method. When
		 * the value returned is <code>null</code> an attempt will be made to
		 * instantiate it using either by using a default value from
		 * {@linkplain #NOBOX} (for primatives) or
		 * {@linkplain Class#newInstance()} on the accessor's
		 * {@linkplain MethodType#returnType()} method.
		 * 
		 * @return the accessor's return target value
		 */
		@SuppressWarnings("unchecked")
		protected F deriveValueFromAccessor() {
			F targetValue = null;
			try {
				targetValue = (F) getAccessor().invoke();
			} catch (final Throwable t) {
				targetValue = null;
			}
			if (targetValue == null) {
				try {
					if (NOBOX.containsKey(getFieldType())) {
						targetValue = (F) NOBOX.get(getFieldType());
					} else {
						targetValue = (F) getAccessor().type().returnType().newInstance();
					}
				} catch (final Exception e) {
					throw new IllegalArgumentException(
							String.format("Unable to get accessor return instance for %1$s using %2$s.", 
									getAccessor(), getAccessor().type().returnType()));
				}
			}
			return targetValue;
		}
		
		/**
		 * Binds a new target to the {@linkplain FieldHandle}
		 * 
		 * @param target
		 *            the target to bind to
		 */
		public void setTarget(final T target) {
			if (getTarget().equals(target)) {
				return;
			}
			this.accessor = getAccessor().bindTo(target);
			this.setter = getSetter().bindTo(target);
			this.target = target;
		}
		
		public T getTarget() {
			return target;
		}

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
		 * @return the declared field type of the property value
		 */
		public Class<F> getDeclaredFieldType() {
			return declaredFieldType;
		}
		
		/**
		 * @return the field type from {@linkplain #getAccessor()} of the
		 *         property value
		 */
		public Class<?> getFieldType() {
			return getAccessor().type().returnType();
		}
	}
}
