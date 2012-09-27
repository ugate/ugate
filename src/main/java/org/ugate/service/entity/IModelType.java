package org.ugate.service.entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

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
	 * Gets a value for a given model
	 * 
	 * @param model
	 *            the model to extract a value from
	 * @return the extracted model value
	 * @throws Throwable
	 *             if failure to extract value occurs
	 */
	public Object getValue(final T model) throws Throwable;

	/**
	 * Value extraction helper
	 */
	public static class ValueHelper {

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
			return buildAccessor(model, type, "get", "is", "has", "use")
					.invoke();
		}

		/**
		 * Sets a {@linkplain Model} value for a {@linkplain IModelType}.
		 * 
		 * @param model
		 *            the {@linkplain Model} to get the value from
		 * @param value
		 *            the value to set
		 * @return the extracted {@linkplain Model} value
		 * @throws Throwable
		 *             any errors during extraction
		 */
		public static <T extends Model> void setValue(final T model,
				final IModelType<T> type, final Object value) throws Throwable {
			final MethodHandle gmh = buildAccessor(model, type, "get", "is",
					"has", "use");
			final String setMethodName = buildMethodName("set", type.getKey());
			final MethodHandle smh = MethodHandles
					.lookup()
					.findVirtual(
							model.getClass(),
							setMethodName,
							MethodType.methodType(void.class, gmh.type()
									.returnType())).bindTo(model);
			smh.invoke(value);
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
	}
}
