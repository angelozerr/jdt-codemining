package org.eclipse.jface.text.revisions.provisional.avatar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class AvatarRepository {

	private static final AvatarRepository INSTANCE = new AvatarRepository();

	private static final String DEFAULT_BASE_URL_URL = "http://www.gravatar.com/avatar/"; //$NON-NLS-1$

	private static final String HASH_REGEX = "[0-9a-f]{32}"; //$NON-NLS-1$

	private static final Pattern HASH_PATTERN = Pattern.compile(HASH_REGEX); // $NON-NLS-1$

	private static final int HASH_LENGTH = 32;

	private static final String HASH_ALGORITHM = "MD5"; //$NON-NLS-1$

	private static final Charset CHARSET = Charset.forName("CP1252"); //$NON-NLS-1$

	private static final int TIMEOUT = 30 * 1000;

	private static final int BUFFER_SIZE = 8192;

	private final Map<String, Avatar> avatars;

	private final String baseURL;

	public static AvatarRepository getInstance() {
		return INSTANCE;
	}

	public AvatarRepository() {
		this(DEFAULT_BASE_URL_URL);
	}

	public AvatarRepository(String baseURL) {
		this.baseURL = baseURL;
		avatars = new HashMap<>();
	}

	public Avatar getAvatarByEmail(String email) {
		return getAvatarByHash(getHash(email));
	}

	public Avatar getAvatarByHash(String hash) {
		try {
			Avatar avatar = this.avatars.get(hash);
			if (avatar != null) {
				return avatar;
			}
			return loadAvatarByHash(hash);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Avatar loadAvatarByHash(String hash) throws IOException {
		if (!isValidHash(hash))
			return null;

		Avatar avatar = null;
		HttpURLConnection connection = (HttpURLConnection) new URL(this.baseURL + hash + "?size=16").openConnection();
		connection.setConnectTimeout(TIMEOUT);
		connection.setUseCaches(false);
		connection.connect();

		if (connection.getResponseCode() != 200)
			return null;

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStream input = connection.getInputStream();
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = -1;
			while ((read = input.read(buffer)) != -1)
				output.write(buffer, 0, read);
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
			}
			try {
				output.close();
			} catch (IOException ignore) {
			}
		}
		avatar = new Avatar(hash, System.currentTimeMillis(), output.toByteArray());
		this.avatars.put(hash, avatar);
		return avatar;
	}

	private boolean isValidHash(String hash) {
		return hash != null && hash.length() == HASH_LENGTH && HASH_PATTERN.matcher(hash).matches();
	}

	/**
	 * Get avatar hash for specified e-mail address
	 * 
	 * @param email
	 * @return hash
	 */
	private static String getHash(String email) {
		String hash = null;
		if (email != null) {
			email = email.trim().toLowerCase(Locale.US);
			if (email.length() > 0)
				hash = digest(email);
		}
		return hash;
	}

	private static String digest(String value) {
		String hashed = null;
		try {
			byte[] digested = MessageDigest.getInstance(HASH_ALGORITHM).digest(value.getBytes(CHARSET));
			hashed = new BigInteger(1, digested).toString(16);
			int padding = HASH_LENGTH - hashed.length();
			if (padding > 0) {
				char[] zeros = new char[padding];
				Arrays.fill(zeros, '0');
				hashed = new String(zeros) + hashed;
			}
		} catch (NoSuchAlgorithmException e) {
			hashed = null;
		}
		return hashed;
	}
}
