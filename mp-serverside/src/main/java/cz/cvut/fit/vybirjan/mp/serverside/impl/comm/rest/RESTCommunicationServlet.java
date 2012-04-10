package cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cvut.fit.vybirjan.mp.serverside.core.LicenseManager;

public class RESTCommunicationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String LICENSE_MANAGER_FACTORY_CLASS_PROP = "cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest.LicenseManagerFactory";
	public static final String FACTORY_METHOD = "createLicenseManager";

	public static final String MARSHALLERS_PROP = "cz.cvut.fit.vybirjan.mp.serverside.impl.comm.rest.Marshallers";
	public static final String MARSHALLERS_SEPARATOR = ";";

	private LicenseManager manager;
	private List<LicenseMarshaller> marshallers;

	@Override
	public void init() throws ServletException {
		String licenseManagerFactoryClass = getInitParameter(LICENSE_MANAGER_FACTORY_CLASS_PROP);
		if (licenseManagerFactoryClass == null) {
			throw new UnavailableException(String.format("Missing license manager factory class name, please specify it in property '%s'",
					LICENSE_MANAGER_FACTORY_CLASS_PROP));
		} else {
			manager = createLicenseManager(licenseManagerFactoryClass);
		}

		String marshallerClasses = getInitParameter(MARSHALLERS_PROP);
		if (marshallerClasses == null) {
			throw new UnavailableException(String.format("No marshallers specified, please provide '%s' property", MARSHALLERS_PROP));
		} else {
			marshallers = createMarshallers(marshallerClasses.split(MARSHALLERS_SEPARATOR));
			if (marshallers.isEmpty()) {
				throw new UnavailableException("No marshallers loaded");
			}
		}

	}

	protected LicenseManager createLicenseManager(String factoryClassName) throws ServletException {
		try {
			Class<?> factoryClass = Class.forName(factoryClassName);
			Method m = factoryClass.getMethod(FACTORY_METHOD);
			LicenseManager ret = (LicenseManager) m.invoke(null);
			if (ret == null) {
				throw new UnavailableException(String.format("License manager factory class '%s' returned null from factory method %s()", factoryClassName,
						FACTORY_METHOD));
			} else {
				return ret;
			}
		} catch (ClassNotFoundException e) {
			throw new UnavailableException(String.format("Failed to load license manager factory class '%s'", e.getMessage()));
		} catch (SecurityException e) {
			throw new UnavailableException(String.format("Faled to load license manager factory class '%s' due to security exception: %s", factoryClassName,
					e.getMessage()));
		} catch (NoSuchMethodException e) {
			throw new UnavailableException(String.format("License manager factory class '%s' does not declare static method '%s'", factoryClassName,
					e.getMessage()));
		} catch (IllegalArgumentException e) {
			throw new UnavailableException(String.format("License manager factory class '%s' does not declare static method '%s' with no arguments: %s",
					factoryClassName, FACTORY_METHOD, e.getMessage()));
		} catch (IllegalAccessException e) {
			throw new UnavailableException(String.format("Method %s from License manager factory class '%s' is not accessible", FACTORY_METHOD,
					factoryClassName));
		} catch (InvocationTargetException e) {
			throw new ServletException(
					String.format("Invocation of method %s from License manager factory class '%s' failed", FACTORY_METHOD, factoryClassName), e);
		} catch (ClassCastException e) {
			throw new UnavailableException(String.format(
					"Invocation of method %s from License manager factory class '%s' returned unexpected class, expected %s, got %s", FACTORY_METHOD,
					factoryClassName, LicenseManager.class.getName(), e.getMessage()));
		}
	}

	protected List<LicenseMarshaller> createMarshallers(String[] classes) {
		ArrayList<LicenseMarshaller> marshallers = new ArrayList<LicenseMarshaller>();

		for (String className : classes) {
			try {
				marshallers.add(createMarshaller(className));
			} catch (Exception e) {
				getServletContext().log(
						String.format("WARN: Failed to instantiate marshaller of class '%s' - %s: %s", className, e.getClass().getName(), e.getMessage()));
			}
		}

		return marshallers;
	}

	protected LicenseMarshaller createMarshaller(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> marshallerClass = Class.forName(className);
		return (LicenseMarshaller) marshallerClass.newInstance();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			System.out.println(req);
		} catch (Exception e) {
			unhandledException(e, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			System.out.println(req);
		} catch (Exception e) {
			unhandledException(e, resp);
		}
	}

	protected void unhandledException(Exception e, HttpServletResponse resp) throws IOException {
		getServletContext().log("Unhandled exception", e);
		resp.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
}
