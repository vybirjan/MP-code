package cz.cvut.fit.vybirjan.mp.web.dao.impl.licensemanager;

import java.security.Key;

import com.google.inject.Inject;

import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;
import cz.cvut.fit.vybirjan.mp.web.dao.EncryptionKeyDAO;
import cz.cvut.fit.vybirjan.mp.web.model.EncryptionKeyJDO;

public class JDOKeyProvider implements ResponseKeyProvider {

	@Inject
	public JDOKeyProvider(EncryptionKeyDAO dao) {
		this.dao = dao;
	}

	private final EncryptionKeyDAO dao;

	@Override
	public Key getResponseEncryptionKey(LicenseRequest request) {
		EncryptionKeyJDO key = dao.findByAppId(request.getApplicationIdx());
		if (key == null) {
			return null;
		} else {
			return key.deserializePrivate();
		}
	}
}
