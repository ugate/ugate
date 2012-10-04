package org.ugate.service.entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {@linkplain Model} type descriptor
 */
public interface IModelType<T extends Model> {

	/**
	 * @return The key to a model field
	 */
	public String getKey();

	/**
	 * @return The name of the {@linkplain IModelType}
	 */
	public String name();

	/**
	 * @return The flag that indicates if the model field is transferable to
	 *         remote nodes
	 */
	public boolean canRemote();

	/**
	 * Gets a value for a given {@linkplain Model}
	 * 
	 * @param model
	 *            the {@linkplain Model} to extract a value from
	 * @return the extracted {@linkplain Model} value
	 * @throws Throwable
	 *             if failure to extract the value occurs
	 */
	public Object getValue(final T model) throws Throwable;

	/**
	 * Sets a value for a given {@linkplain Model}
	 * 
	 * @param model
	 *            the {@linkplain Model}
	 * @param value
	 *            the value to set
	 * @throws Throwable
	 *             if failure to set the value occurs
	 */
	public void setValue(final T model, Object value) throws Throwable;

	/**
	 * Value extraction helper
	 */
	public static class ValueHelper {

		public static final String[] ACCESSOR_PREFIXES = { "get", "is", "has",
				"use" };
		private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		private static final Map<Class<?>, Class<?>> PRIMS = new HashMap<>();
		static {
			PRIMS.put(boolean.class, Boolean.class);
			PRIMS.put(char.class, Character.class);
			PRIMS.put(double.class, Double.class);
			PRIMS.put(float.class, Float.class);
			PRIMS.put(long.class, Long.class);
			PRIMS.put(int.class, Integer.class);
			PRIMS.put(short.class, Short.class);
			PRIMS.put(long.class, Long.class);
			PRIMS.put(byte.class, Byte.class);
		}
		private static final Map<Class<?>, Object> DFLTS = new HashMap<>();
		static {
			DFLTS.put(Boolean.class, Boolean.FALSE);
			DFLTS.put(boolean.class, false);
			DFLTS.put(Byte.class, Byte.valueOf("0"));
			DFLTS.put(byte.class, Byte.valueOf("0").byteValue());
			DFLTS.put(Number.class, 0L);
			DFLTS.put(Short.class, Short.valueOf("0"));
			DFLTS.put(short.class, Short.valueOf("0").shortValue());
			DFLTS.put(Character.class, Character.valueOf(' '));
			DFLTS.put(char.class, ' ');
			DFLTS.put(Integer.class, Integer.valueOf(0));
			DFLTS.put(int.class, 0);
			DFLTS.put(Long.class, Long.valueOf(0));
			DFLTS.put(long.class, 0L);
			DFLTS.put(Float.class, Float.valueOf(0F));
			DFLTS.put(float.class, 0F);
			DFLTS.put(Double.class, Double.valueOf(0D));
			DFLTS.put(double.class, 0D);
			DFLTS.put(BigInteger.class, BigInteger.valueOf(0L));
			DFLTS.put(BigDecimal.class, BigDecimal.valueOf(0D));
		}

		/**
		 * Gets a {@linkplain Model} value for a {@linkplain IModelType}
		 * 
		 * @param model
		 *            the {@linkplain Model} to get the value from
		 * @return the extracted {@linkplain Model} value
		 * @throws Throwable
		 *             any errors during extraction
		 */
		public static <T extends Model> Object getValue(final T model,
				final IModelType<T> type) throws Throwable {
			return buildAccessor(model, type, ACCESSOR_PREFIXES).invoke();
		}

		/**
		 * Sets a {@linkplain Model} value for a {@linkplain IModelType}.
		 * 
		 * @param model
		 *            the {@linkplain Model} to get the value from
		 * @param type
		 *            the {@linkplain Model} type
		 * @param value
		 *            the value to set
		 * @return the extracted {@linkplain Model} value
		 * @throws Throwable
		 *             any errors during extraction
		 */
		public static <T extends Model> void setValue(final T model,
				final IModelType<T> type, final Object value) throws Throwable {
			final MethodHandle gmh = buildAccessor(model, type,
					ACCESSOR_PREFIXES);
			final String setMethodName = buildMethodName("set", type.getKey());
			final MethodHandle smh = MethodHandles
					.lookup()
					.findVirtual(
							model.getClass(),
							setMethodName,
							MethodType.methodType(void.class, gmh.type()
									.returnType())).bindTo(model);
			final Object objVal = coerce(value, gmh.type().returnType());
			smh.invoke(objVal);
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
		private static String buildMethodName(final String prefix,
				final String fieldName) {
			return (fieldName.startsWith(prefix) ? fieldName : prefix
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1));
		}

		/**
		 * Attempts to build a {@linkplain MethodHandle} accessor for the field
		 * name using common prefixes used for methods to access a field
		 * 
		 * @param model
		 *            the target object that the accessor is for
		 * @param fieldName
		 *            the field name that the accessor is for
		 * @return the accessor {@linkplain MethodHandle}
		 * @param fieldNamePrefix
		 *            the prefix of the method for the field name
		 * @return the accessor {@linkplain MethodHandle}
		 */
		private static <T extends Model> MethodHandle buildAccessor(
				final T model, final IModelType<T> type,
				final String... fieldNamePrefix) {
			final String accessorName = buildMethodName(fieldNamePrefix[0],
					type.getKey());
			try {
				return MethodHandles
						.lookup()
						.findVirtual(
								model.getClass(),
								accessorName,
								MethodType.methodType(model.getClass()
										.getMethod(accessorName)
										.getReturnType())).bindTo(model);
			} catch (final NoSuchMethodException e) {
				return fieldNamePrefix.length <= 1 ? null : buildAccessor(
						model, type, Arrays.copyOfRange(fieldNamePrefix, 1,
								fieldNamePrefix.length));
			} catch (final Throwable t) {
				throw new IllegalArgumentException(
						"Unable to resolve accessor " + accessorName, t);
			}
		}

		/**
		 * Attempts to coerce a value into the specified class
		 * 
		 * @param v
		 *            the value to coerce
		 * @param targetClass
		 *            the class to coerce to
		 * @return the coerced value (null when value failed to be coerced)
		 */
		@SuppressWarnings("unchecked")
		public static <VT> VT coerce(final Object v, final Class<VT> targetClass) {
			if (targetClass == Object.class) {
				return (VT) v;
			}
			VT val;
			final boolean isStringType = targetClass.equals(String.class);
			if (v == null || (!isStringType && v.toString().isEmpty())) {
				val = (VT) defaultValue(targetClass);
			} else if (isStringType
					|| (v != null && targetClass.isAssignableFrom(v.getClass()))) {
				val = (VT) targetClass.cast(v);
			} else if (v != null && Date.class.isAssignableFrom(targetClass)) {
				if (Calendar.class.isAssignableFrom(v.getClass())) {
					val = (VT) ((Calendar) v).getTime();
				} else {
					try {
						val = (VT) SDF.parse(v.toString());
					} catch (final Throwable t) {
						throw new IllegalArgumentException(String.format(
								"Unable to convert %1$s to %2$s", v,
								targetClass), t);
					}
				}
			} else if (v != null
					&& Calendar.class.isAssignableFrom(targetClass)) {
				final Calendar cal = Calendar.getInstance();
				Date date = null;
				try {
					date = Date.class.isAssignableFrom(v.getClass()) ? (Date) v
							: SDF.parse(v.toString());
					cal.setTime(date);
					val = (VT) cal;
				} catch (final Throwable t) {
					throw new IllegalArgumentException(String.format(
							"Unable to convert %1$s to %2$s", v, targetClass),
							t);
				}
			} else {
				val = valueOf(targetClass, v.toString());
			}
			return val;
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
			return (VT) (DFLTS.containsKey(clazz) ? DFLTS.get(clazz) : null);
		}

		/**
		 * Attempts to invoke a <code>valueOf</code> using the specified class
		 * 
		 * @param valueOfClass
		 *            the class to attempt to invoke a <code>valueOf</code>
		 *            method on
		 * @param value
		 *            the value to invoke the <code>valueOf</code> method on
		 * @return the result (null if the operation fails)
		 */
		@SuppressWarnings("unchecked")
		public static <VT> VT valueOf(final Class<VT> valueOfClass,
				final Object value) {
			if (value != null && String.class.isAssignableFrom(valueOfClass)) {
				return (VT) value.toString();
			}
			final Class<?> clazz = PRIMS.containsKey(valueOfClass) ? PRIMS.get(valueOfClass) : valueOfClass;
			MethodHandle mh1 = null;
			try {
				mh1 = MethodHandles.lookup().findStatic(
						clazz, "valueOf",
						MethodType.methodType(clazz, String.class));
			} catch (final Throwable t) {
				// class doesn't support it- do nothing
			}
			if (mh1 != null) {
				try {
					return (VT) mh1.invoke(value);
				} catch (final Throwable t) {
					throw new IllegalArgumentException(String.format(
							"Unable to invoke valueOf on %1$s using %2$s",
							value, valueOfClass), t);
				}
			}
			return null;
		}
	}
}
