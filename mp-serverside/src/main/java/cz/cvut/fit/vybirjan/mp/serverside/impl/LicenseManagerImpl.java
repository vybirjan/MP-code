package cz.cvut.fit.vybirjan.mp.serverside.impl;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cz.cvut.fit.vybirjan.mp.common.Utils;
import cz.cvut.fit.vybirjan.mp.common.comm.HardwareFingerprint;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseInformation;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseRequest;
import cz.cvut.fit.vybirjan.mp.common.comm.LicenseResponse;
import cz.cvut.fit.vybirjan.mp.common.crypto.TaggedKey;
import cz.cvut.fit.vybirjan.mp.serverside.core.DataSource;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseEventHandler;
import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;
import cz.cvut.fit.vybirjan.mp.serverside.core.RequestProcessingContext;
import cz.cvut.fit.vybirjan.mp.serverside.core.ResponseKeyProvider;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Activation;
import cz.cvut.fit.vybirjan.mp.serverside.domain.EntityFactory;
import cz.cvut.fit.vybirjan.mp.serverside.domain.Feature;
import cz.cvut.fit.vybirjan.mp.serverside.domain.License;

public class LicenseManagerImpl implements LicenseManager {

	public LicenseManagerImpl(DataSource dataSource, EntityFactory entityFactory) {
		this(dataSource, entityFactory, null);
	}

	public LicenseManagerImpl(DataSource dataSource, EntityFactory entityFactory, ResponseKeyProvider keyProvider) {
		this.dataSource = dataSource;
		this.entityFactory = entityFactory;
		this.keyProvider = keyProvider;
	}

	protected final DataSource dataSource;
	protected final EntityFactory entityFactory;
	protected final ResponseKeyProvider keyProvider;

	protected List<LicenseEventHandler> eventHandlers = Collections.synchronizedList(new LinkedList<LicenseEventHandler>());

	@Override
	public LicenseResponse activateLicense(LicenseRequest request) {
		try {
			RequestProcessingContenxtImpl processingContext = new RequestProcessingContenxtImpl(eventHandlers.iterator(), BASE_HANDLER);
			LicenseResponse response = processingContext.processActivateLicense(request);

			signResponse(request, response);

			return response;
		} catch (RuntimeException e) {
			return LicenseResponse.internalError();
		}
	}

	protected void signResponse(LicenseRequest request, LicenseResponse response) {
		if (response.getLicenseInformation() != null && keyProvider != null) {
			Key k = keyProvider.getResponseEncryptionKey(request);
			if (k != null) {
				response.getLicenseInformation().sign(k);
			}
		}
	}

	public void addLicenseEventHandler(LicenseEventHandler handler) {
		if (!eventHandlers.contains(handler)) {
			eventHandlers.add(handler);
		}
	}

	public boolean removeLicenseEventHandler(LicenseEventHandler handler) {
		return eventHandlers.remove(handler);
	}

	private final LicenseEventHandler BASE_HANDLER = new LicenseEventHandler() {

		@Override
		public LicenseResponse handleActivateLicense(LicenseRequest request, RequestProcessingContext context) {
			return LicenseManagerImpl.this.handleActivateLicense(request);
		}

		@Override
		public LicenseResponse handleGetExistingLicense(LicenseRequest request, RequestProcessingContext context) {
			return LicenseManagerImpl.this.handleGetExistingLicense(request);
		}
	};

	private LicenseResponse handleActivateLicense(LicenseRequest request) {
		License license = dataSource.findByNumber(request.getLicenseNumber());

		if (license == null) {
			return LicenseResponse.licenseNotFound();
		}

		if (!license.isActive()) {
			return LicenseResponse.inactive();
		}

		if (!Utils.isValid(license.getValidFrom(), license.getValidTo())) {
			return LicenseResponse.expired();
		}

		Activation existingActivation = dataSource.findActiveActivationForLicense(license, request.getFingerprints());
		if (existingActivation == null) {
			if (license.isAllowNewActivations()) {
				return createNewActivation(license, request.getFingerprints());
			} else {
				return LicenseResponse.newActivationsNotAllowed();
			}

		} else {
			return LicenseResponse.foundExisting(createResponseInfo(license, existingActivation));
		}
	}

	private LicenseResponse createNewActivation(License license, List<HardwareFingerprint> fingerprints) {
		Activation activation = entityFactory.createActivation(fingerprints);

		dataSource.addActivationToLicense(license, activation);

		return LicenseResponse.createdNew(createResponseInfo(license, activation));
	}

	private LicenseInformation createResponseInfo(License l, Activation a) {
		LicenseInformation info = new LicenseInformation();
		info.setLicenseNumber(l.getNumber());

		Collection<Feature> features = dataSource.findFeaturesForLicense(l);

		for (Feature f : features) {
			if (Utils.isValid(f.getValidFrom(), f.getValidTo())) {
				info.addFeature(new cz.cvut.fit.vybirjan.mp.common.comm.Feature(f.getCode(),
						Utils.max(f.getValidFrom(), l.getValidFrom()),
						Utils.min(f.getValidFrom(), f.getValidTo())));

				for (TaggedKey key : f.getKeys()) {
					info.addKey(key);
				}
			}
		}

		for (HardwareFingerprint fp : a.getFingerprints()) {
			info.addFingerPrint(fp);
		}

		return info;
	}

	private LicenseResponse handleGetExistingLicense(LicenseRequest request) {
		return null;
	}

	@Override
	public LicenseResponse getLicense(LicenseRequest request) {
		try {
			RequestProcessingContenxtImpl processingContext = new RequestProcessingContenxtImpl(eventHandlers.iterator(), BASE_HANDLER);
			LicenseResponse response = processingContext.processGetExistingLicense(request);

			signResponse(request, response);

			return response;
		} catch (RuntimeException e) {
			return LicenseResponse.internalError();
		}
	}
}
