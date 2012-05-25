package org.ugate.service.entity;

import java.io.Serializable;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

/**
 * Common domain model
 */
public abstract class Model implements Serializable {

	private static final long serialVersionUID = 2577386054919013230L;
	
	@Transient
	private ModelListener listener;

	@PrePersist
	protected void prePersist() {
		if (listener != null) {
			listener.prePersist();
		}
	}

	@PostPersist
	protected void postPersist() {
		if (listener != null) {
			listener.postPersist();
		}
	}

	@PreRemove
	protected void preRemove() {
		if (listener != null) {
			listener.preRemove();
		}
	}

	@PostRemove
	protected void postRemove() {
		if (listener != null) {
			listener.postRemove();
		}
	}

	@PreUpdate
	protected void preUpdate() {
		if (listener != null) {
			listener.preUpdate();
		}
	}

	@PostUpdate
	protected void postUpdate() {
		if (listener != null) {
			listener.postUpdate();
		}
	}

	@PostLoad
	protected void postLoad() {
		if (listener != null) {
			listener.postLoad();
		}
	}
	
	/**
	 * @return the listener
	 */
	@Transient
	protected ModelListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	@Transient
	protected void setListener(final ModelListener listener) {
		this.listener = listener;
	}

	/**
	 * {@linkplain Model} listener
	 */
	public static interface ModelListener {
		/**
		 * @see PrePersist
		 */
		public void prePersist();
		/**
		 * @see PostPersist
		 */
		public void postPersist();
		/**
		 * @see PreRemove
		 */
		public void preRemove();
		/**
		 * @see PostRemove
		 */
		public void postRemove();
		/**
		 * @see PreUpdate
		 */
		public void preUpdate();
		/**
		 * @see PostUpdate
		 */
		public void postUpdate();
		/**
		 * @see PostLoad
		 */
		public void postLoad();
	}
}
