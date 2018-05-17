/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiwitech.challenge.cors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for CORS request handling based on the
 * <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public abstract class CorsUtils {

	private static final String SCHEME_PATTERN = "([^:/?#]+):";

	private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";

	private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";

	private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]";

	private static final String HOST_PATTERN = "(" + HOST_IPV6_PATTERN + "|" + HOST_IPV4_PATTERN + ")";

	private static final String PORT_PATTERN = "(\\d*(?:\\{[^/]+?\\})?)";

	private static final String PATH_PATTERN = "([^?#]*)";

	private static final String QUERY_PATTERN = "([^#]*)";

	private static final String LAST_PATTERN = "(.*)";

	// Regex patterns that matches URIs. See RFC 3986, appendix B
	private static final Pattern URI_PATTERN = Pattern.compile(
			"^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
					")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

	private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("host=\"?([^;,\"]+)\"?");

	private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("proto=\"?([^;,\"]+)\"?");

	/**
	 * The HTTP {@code Origin} header field name.
	 * @see <a href="http://tools.ietf.org/html/rfc6454">RFC 6454</a>
	 */
	public static final String ORIGIN = "Origin";

	/**
	 * The CORS {@code Access-Control-Allow-Credentials} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	/**
	 * The CORS {@code Access-Control-Allow-Headers} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	/**
	 * The CORS {@code Access-Control-Allow-Methods} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	/**
	 * The CORS {@code Access-Control-Allow-Origin} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	/**
	 * The CORS {@code Access-Control-Expose-Headers} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	/**
	 * The CORS {@code Access-Control-Max-Age} response header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	/**
	 * The CORS {@code Access-Control-Request-Headers} request header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	/**
	 * The CORS {@code Access-Control-Request-Method} request header field name.
	 * @see <a href="http://www.w3.org/TR/cors/">CORS W3C recommandation</a>
	 */
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

	/**
	 * The HTTP {@code Vary} header field name.
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-7.1.4">Section 7.1.4 of RFC 7231</a>
	 */
	public static final String VARY = "Vary";

	/**
	 * Returns {@code true} if the request is a valid CORS one.
	 */
	public static boolean isCorsRequest(HttpServletRequest request) {
		return (request.getHeader(ORIGIN) != null);
	}

	/**
	 * Returns {@code true} if the request is a valid CORS pre-flight one.
	 */
	public static boolean isPreFlightRequest(HttpServletRequest request) {
		return (isCorsRequest(request) && matches(HttpMethod.OPTIONS, request.getMethod()) &&
				request.getHeader(ACCESS_CONTROL_REQUEST_METHOD) != null);
	}

	// Utility methods not included in referenced classes at this version....

	static <E> boolean isEmpty(Collection<E> collection) {
		return collection == null || collection.isEmpty();
	}

	private static final Map<String, HttpMethod> mappings = new HashMap<>(HttpMethod.values().length);

	static {
		for (HttpMethod httpMethod : HttpMethod.values()) {
			mappings.put(httpMethod.name(), httpMethod);
		}
	}

	/**
	 * Resolve the given method value to an {@code HttpMethod}.
	 * @param method the method value as a String
	 * @return the corresponding {@code HttpMethod}, or {@code null} if not found
	 * @since 4.2.4
	 */
	static HttpMethod resolve(String method) {
		return (method != null ? mappings.get(method) : null);
	}

	/**
	 * Determine whether this {@code HttpMethod} matches the given
	 * method value.
	 * @param method the method value as a String
	 * @return {@code true} if it matches, {@code false} otherwise
	 * @since 4.2.4
	 */
	static boolean matches(HttpMethod base, String method) {
		return base.name().equals(method);
	}

	/**
	 * Check if the request is a same-origin one, based on {@code Origin}, {@code Host},
	 * {@code Forwarded} and {@code X-Forwarded-Host} headers.
	 * @return {@code true} if the request is a same-origin one, {@code false} in case
	 * of cross-origin request.
	 * @since 4.2
	 */
	public static boolean isSameOrigin(HttpRequest request) {
		String origin = getOrigin(request.getHeaders());
		if (origin == null) {
			return true;
		}
		UriComponents actualUrl = fromHttpRequest(request).build();
		UriComponents originUrl = fromOriginHeader(origin).build();
		return (actualUrl.getHost().equals(originUrl.getHost()) && getPort(actualUrl) == getPort(originUrl));
	}

	/**
	 * Create a new {@code UriComponents} object from the URI associated with
	 * the given HttpRequest while also overlaying with values from the headers
	 * "Forwarded" (<a href="http://tools.ietf.org/html/rfc7239">RFC 7239</a>, or
	 * "X-Forwarded-Host", "X-Forwarded-Port", and "X-Forwarded-Proto" if "Forwarded" is
	 * not found.
	 * @param request the source request
	 * @return the URI components of the URI
	 * @since 4.1.5
	 */
	public static UriComponentsBuilder fromHttpRequest(HttpRequest request) {
		URI uri = request.getURI();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);

		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();

		String forwardedHeader = request.getHeaders().getFirst("Forwarded");
		if (StringUtils.hasText(forwardedHeader)) {
			String forwardedToUse = StringUtils.commaDelimitedListToStringArray(forwardedHeader)[0];
			Matcher m = FORWARDED_HOST_PATTERN.matcher(forwardedToUse);
			if (m.find()) {
				host = m.group(1).trim();
			}
			m = FORWARDED_PROTO_PATTERN.matcher(forwardedToUse);
			if (m.find()) {
				scheme = m.group(1).trim();
			}
		}
		else {
			String hostHeader = request.getHeaders().getFirst("X-Forwarded-Host");
			if (StringUtils.hasText(hostHeader)) {
				String[] hosts = StringUtils.commaDelimitedListToStringArray(hostHeader);
				String hostToUse = hosts[0];
				if (hostToUse.contains(":")) {
					String[] hostAndPort = StringUtils.split(hostToUse, ":");
					host = hostAndPort[0];
					port = Integer.parseInt(hostAndPort[1]);
				}
				else {
					host = hostToUse;
					port = -1;
				}
			}

			String portHeader = request.getHeaders().getFirst("X-Forwarded-Port");
			if (StringUtils.hasText(portHeader)) {
				String[] ports = StringUtils.commaDelimitedListToStringArray(portHeader);
				port = Integer.parseInt(ports[0]);
			}

			String protocolHeader = request.getHeaders().getFirst("X-Forwarded-Proto");
			if (StringUtils.hasText(protocolHeader)) {
				String[] protocols = StringUtils.commaDelimitedListToStringArray(protocolHeader);
				scheme = protocols[0];
			}
		}

		builder.scheme(scheme);
		builder.host(host);
		builder.port(-1);
		if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
			builder.port(port);
		}
		return builder;
	}


	/**
	 * Create an instance by parsing the "Origin" header of an HTTP request.
	 * @see <a href="https://tools.ietf.org/html/rfc6454">RFC 6454</a>
	 */
	public static UriComponentsBuilder fromOriginHeader(String origin) {
		Matcher matcher = URI_PATTERN.matcher(origin);
		if (matcher.matches()) {
			UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
			String scheme = matcher.group(2);
			String host = matcher.group(6);
			String port = matcher.group(8);
			if (StringUtils.hasLength(scheme)) {
				builder.scheme(scheme);
			}
			builder.host(host);
			if (StringUtils.hasLength(port)) {
				builder.port(Integer.parseInt(port));
			}
			return builder;
		}
		else {
			throw new IllegalArgumentException("[" + origin + "] is not a valid \"Origin\" header value");
		}
	}

	private static int getPort(UriComponents component) {
		int port = component.getPort();
		if (port == -1) {
			if ("http".equals(component.getScheme()) || "ws".equals(component.getScheme())) {
				port = 80;
			}
			else if ("https".equals(component.getScheme()) || "wss".equals(component.getScheme())) {
				port = 443;
			}
		}
		return port;
	}

	protected static String toCommaDelimitedString(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String ifNoneMatch = iterator.next();
			builder.append(ifNoneMatch);
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	protected static List<String> getFirstValueAsList(HttpHeaders headers, String header) {
		List<String> result = new ArrayList<String>();
		String value = headers.getFirst(header);
		if (value != null) {
			String[] tokens = value.split(",\\s*");
			for (String token : tokens) {
				result.add(token);
			}
		}
		return result;
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Credentials} response header.
	 */
	public static void setAccessControlAllowCredentials(HttpHeaders headers, boolean allowCredentials) {
		headers.set(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(allowCredentials));
	}

	/**
	 * Returns the value of the {@code Access-Control-Allow-Credentials} response header.
	 */
	public static boolean getAccessControlAllowCredentials(HttpHeaders headers) {
		return new Boolean(headers.getFirst(ACCESS_CONTROL_ALLOW_CREDENTIALS));
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Headers} response header.
	 */
	public static void setAccessControlAllowHeaders(HttpHeaders headers, List<String> allowedHeaders) {
		headers.set(ACCESS_CONTROL_ALLOW_HEADERS, toCommaDelimitedString(allowedHeaders));
	}

	/**
	 * Returns the value of the {@code Access-Control-Allow-Headers} response header.
	 */
	public static List<String> getAccessControlAllowHeaders(HttpHeaders headers) {
		return getFirstValueAsList(headers, ACCESS_CONTROL_ALLOW_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Methods} response header.
	 */
	public static void setAccessControlAllowMethods(HttpHeaders headers, List<HttpMethod> allowedMethods) {
		headers.set(ACCESS_CONTROL_ALLOW_METHODS, StringUtils.collectionToCommaDelimitedString(allowedMethods));
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Methods} response header.
	 */
	public static List<HttpMethod> getAccessControlAllowMethods(HttpHeaders headers) {
		List<HttpMethod> result = new ArrayList<HttpMethod>();
		String value = headers.getFirst(ACCESS_CONTROL_ALLOW_METHODS);
		if (value != null) {
			String[] tokens = value.split(",\\s*");
			for (String token : tokens) {
				HttpMethod resolved = resolve(token);
				if (resolved != null) {
					result.add(resolved);
				}
			}
		}
		return result;
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Allow-Origin} response header.
	 */
	public static void setAccessControlAllowOrigin(HttpHeaders headers, String allowedOrigin) {
		headers.set(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
	}

	/**
	 * Return the value of the {@code Access-Control-Allow-Origin} response header.
	 */
	public static String getAccessControlAllowOrigin(HttpHeaders headers) {
		return headers.getFirst(ACCESS_CONTROL_ALLOW_ORIGIN);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Expose-Headers} response header.
	 */
	public static void setAccessControlExposeHeaders(HttpHeaders headers, List<String> exposedHeaders) {
		headers.set(ACCESS_CONTROL_EXPOSE_HEADERS, toCommaDelimitedString(exposedHeaders));
	}

	/**
	 * Returns the value of the {@code Access-Control-Expose-Headers} response header.
	 */
	public static List<String> getAccessControlExposeHeaders(HttpHeaders headers) {
		return getFirstValueAsList(headers, ACCESS_CONTROL_EXPOSE_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Max-Age} response header.
	 */
	public static void setAccessControlMaxAge(HttpHeaders headers, long maxAge) {
		headers.set(ACCESS_CONTROL_MAX_AGE, Long.toString(maxAge));
	}

	/**
	 * Returns the value of the {@code Access-Control-Max-Age} response header.
	 * <p>Returns -1 when the max age is unknown.
	 */
	public static long getAccessControlMaxAge(HttpHeaders headers) {
		String value = headers.getFirst(ACCESS_CONTROL_MAX_AGE);
		return (value != null ? Long.parseLong(value) : -1);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Request-Headers} request header.
	 */
	public static void setAccessControlRequestHeaders(HttpHeaders headers, List<String> requestHeaders) {
		headers.set(ACCESS_CONTROL_REQUEST_HEADERS, toCommaDelimitedString(requestHeaders));
	}

	/**
	 * Returns the value of the {@code Access-Control-Request-Headers} request header.
	 */
	public static List<String> getAccessControlRequestHeaders(HttpHeaders headers) {
		return getFirstValueAsList(headers, ACCESS_CONTROL_REQUEST_HEADERS);
	}

	/**
	 * Set the (new) value of the {@code Access-Control-Request-Method} request header.
	 */
	public static void setAccessControlRequestMethod(HttpHeaders headers, HttpMethod requestedMethod) {
		headers.set(ACCESS_CONTROL_REQUEST_METHOD, requestedMethod.name());
	}

	/**
	 * Return the value of the {@code Access-Control-Request-Method} request header.
	 */
	public static HttpMethod getAccessControlRequestMethod(HttpHeaders headers) {
		return resolve(headers.getFirst(ACCESS_CONTROL_REQUEST_METHOD));
	}

	/**
	 * Set the (new) value of the {@code Origin} header.
	 */
	public static void setOrigin(HttpHeaders headers, String origin) {
		headers.set(ORIGIN, origin);
	}

	/**
	 * Return the value of the {@code Origin} header.
	 */
	public static String getOrigin(HttpHeaders headers) {
		return headers.getFirst(ORIGIN);
	}

}
