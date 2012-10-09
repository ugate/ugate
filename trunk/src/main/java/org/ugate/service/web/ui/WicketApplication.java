package org.ugate.service.web.ui;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.core.request.mapper.CryptoMapper;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * {@linkplain WebApplication} implementation
 */
public class WicketApplication extends WebApplication {

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<IndexPage> getHomePage() {
		return IndexPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
		super.init();
		getMarkupSettings().setStripWicketTags(true);
        setRootRequestMapper(new CryptoMapper(getRootRequestMapper(), this));
//        getRootRequestMapperAsCompound().add(
//			new MountMapper("/", new PackageMapper(
//				PackageName.forClass(IndexPage.class))));
//        mountPackage("/", IndexPage.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}
}
