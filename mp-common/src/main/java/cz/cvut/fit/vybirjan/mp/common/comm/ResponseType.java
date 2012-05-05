package cz.cvut.fit.vybirjan.mp.common.comm;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Enumeration of different types of responses sent by license server.
 * 
 * @author Jan Vybíral
 * 
 */
@XmlEnum
public enum ResponseType {
	/**
	 * License was found and current activation is still valid.
	 */
	OK_EXISTING_VERIFIED,
	/**
	 * License was found and new activation was created.
	 */
	OK_NEW_CREATED,
	/**
	 * Requested license does not allow new activations.
	 */
	ERROR_NEW_ACTIVATIONS_NOT_ALLOWED,
	/**
	 * Requested license was deactivated.
	 */
	ERROR_INACTIVE,
	/**
	 * Requested license was not activated (no activations have been made)
	 */
	ERROR_NOT_ACTIVATED,
	/**
	 * Internal server error occurred when handling request.
	 */
	ERROR_INTERNAL_ERROR,
	/**
	 * License was activated too many times.
	 */
	ERROR_TOO_MANY_ACTIVATIONS,
	/**
	 * Requested license was not found.
	 */
	ERROR_LICENSE_NOT_FOUND,
	/**
	 * Requested license expired
	 */
	ERROR_EXPIRED,
	/**
	 * Request sent by client is not valid and server refuses to perform it.
	 */
	ERROR_BAD_REQUEST,
	/**
	 * Error during communication with license server.
	 */
	ERROR_COMMUNICATION_ERROR
}