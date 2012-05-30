package org.ugate.gui.components;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Iterator;

import javafx.beans.property.ObjectPropertyBase;

import javax.jms.IllegalStateException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ugate.service.entity.jpa.Actor;

public class PathProperty<B, T> extends ObjectPropertyBase<T> {

	private final String fieldPath;
	private PropertyMethodHandles propMethHandles;
	private final B bean;
	
	public static void main(final String[] args) throws Exception {
		final Actor actor = new Actor();
		final PathProperty<Actor, String> prop = new PathProperty<Actor, String>(
				actor, "host.mailInboxName", String.class);
		prop.set("My mailbox");
		System.out.println("Property Value: " + prop.get());
		System.out.println("Model Get: "
				+ actor.getHost().getMailInboxName());
	}

	public PathProperty(final B bean, final String fieldPath, final Class<T> type) {
		super();
		this.bean = bean;
		this.fieldPath = fieldPath;
		addBidirectionalBinding();
		try {
			this.propMethHandles = PropertyMethodHandles.build(getBean(), getName());
		} catch (final Throwable t) {
			throw new RuntimeException(String.format(
					"Unable to instantiate expression %1$s on %2$s", 
					getBean(), getName()), t);
		}
	}

	protected void addBidirectionalBinding() {
		final String packagePath = getBean().getClass().getPackage().getName().replaceAll("\\.", "/");
		final String setterName = PropertyMethodHandles.buildMethodName("set",
				"mailInboxName");//getPropMethHandles().getFieldName());
		final InputStream is = getBean().getClass().getResourceAsStream(
				getBean().getClass().getSimpleName() + ".class");
		try {
			final ClassReader cr = new ClassReader(is);
			final ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			MethodNode mn;
			for (final Object mno : cn.methods) {
				mn = (MethodNode) mno;
				if (mn.name.equals(setterName)) {
					final InsnList setBeanValueLst = new InsnList();
					setBeanValueLst.add(new LdcInsnNode(mn.name));
					setBeanValueLst.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
							packagePath, "set",
							"(Ljava/lang/Object;)V"));
//					Iterator<AbstractInsnNode> insnNodes = mn.instructions.iterator();
//					while (insnNodes.hasNext()) {
//						System.out.println(insnNodes.next().getOpcode());
//					}
					mn.instructions.insert(setBeanValueLst);
				}
			}
			final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS
					| ClassWriter.COMPUTE_FRAMES);
			cn.accept(cw);
			final File outDir = new File(getBean().getClass().getPackage().getName().replaceAll("\\.", "/"));
			outDir.mkdirs();
			final DataOutputStream dout = new DataOutputStream(new FileOutputStream(
					new File(outDir, getBean().getClass().getSimpleName()
							+ ".class")));
			dout.write(cw.toByteArray());
			dout.flush();
			dout.close();
		} catch (final Throwable t) {
			new RuntimeException("Unable to bind from: " + getBean(), t);
		}
	}

	@Override
	public void set(T v) {
		try {
			getPropMethHandles().getSetter().invoke(v);
			super.set(v);
		} catch (final Throwable t) {
			throw new RuntimeException("Unable to set value: " + v, t);
		}
	};

	@Override
	public T get() {
		try {
			return (T) getPropMethHandles().getAccessor().invoke();
			//return super.get();
		} catch (final Throwable t) {
			throw new RuntimeException("Unable to get value", t);
		}
	}

	@Override
	public B getBean() {
		return bean;
	}
	
	public PropertyMethodHandles getPropMethHandles() {
		return propMethHandles;
	}

	@Override
	public String getName() {
		return fieldPath;
	}
	
	public static class PropertyMethodHandles {

		private final String fieldName;
		private final MethodHandle accessor;
		private final MethodHandle setter;
		private Object setterArgument;

		protected PropertyMethodHandles(final Object target, final String fieldName,
				final boolean insertSetterArgument) throws NoSuchMethodException {
			this.fieldName = fieldName;
			this.accessor = buildGetter(target, fieldName);
			this.setter = buildSetter(getAccessor(), target, fieldName, insertSetterArgument);
		}
		
		public static PropertyMethodHandles build(final Object initialTarget, 
				final String expString) throws NoSuchMethodException, IllegalStateException {
			final String[] expStr = expString.split("\\.");
			Object target = initialTarget;
			PropertyMethodHandles pmh = null;
			for (int i = 0; i < expStr.length; i++) {
				pmh = new PropertyMethodHandles(target, expStr[i], i < (expStr.length - 1));
				target = pmh.getSetterArgument();
			}
			return pmh;
		}
		
		protected MethodHandle buildGetter(final Object target, final String fieldName) 
						throws NoSuchMethodException {
			final MethodHandle mh = buildAccessor(target, fieldName, "get", "is", "has");
			if (mh == null) {
				throw new NoSuchMethodException(fieldName);
			}
			return mh;
		}
		
		protected MethodHandle buildSetter(final MethodHandle accessor, 
				final Object target, final String fieldName, 
				final boolean insertSetterArgument) {
			if (insertSetterArgument) {
				try {
					this.setterArgument = accessor.invoke();
				} catch (final Throwable t) {
					this.setterArgument = null;
				}
				if (getSetterArgument() == null) {
					try {
						this.setterArgument = accessor.type().returnType().newInstance();
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
				if (getSetterArgument() != null) {
//					final MethodHandle mh2 = MethodHandles.insertArguments(mh1, 0, 
//							getSetterArgument());
					mh1.invoke(getSetterArgument());
					return mh1;
				}
				return mh1;
			} catch (final Throwable t) {
				throw new IllegalArgumentException("Unable to resolve setter "
						+ fieldName, t);
			}
		}

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
		
		public static String buildMethodName(final String prefix, 
				final String fieldName) {
			return (fieldName.startsWith(prefix) ? fieldName : prefix + 
				fieldName.substring(0, 1).toUpperCase() + 
					fieldName.substring(1));
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
		public MethodHandle getAccessor() {
			return accessor;
		}

		/**
		 * @return the setter
		 */
		public MethodHandle getSetter() {
			return setter;
		}

		/**
		 * @return the argument that will be used by the
		 *         {@linkplain #getSetter()}
		 */
		public Object getSetterArgument() {
			return setterArgument;
		}
	}
}
