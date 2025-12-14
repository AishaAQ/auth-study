package utilities;

import java.security.SecureRandom;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class Hashing {
	
	
	/**
	 * Recommended parameters as per https://thecopenhagenbook.com/password-authentication
	 */
	private static final int TYPE = Argon2Parameters.ARGON2_id;
	private static final int VERSION = Argon2Parameters.ARGON2_VERSION_13;
	private static final int MEMORY_SIZE = 19456;
	private static final int ITERATIONS = 2;
	private static final int PARALLELISM = 1;
	
	
	private static final int saltByteSize = 16;
	
	private static byte[] generateSalt() {
		
	    SecureRandom secureRandom = new SecureRandom();
	    byte[] salt = new byte[saltByteSize];
	    secureRandom.nextBytes(salt);
	        
	    return salt;
	}
	
	
	public static String generateHash(String password) {
		
		byte[] salt = generateSalt();
		
		Argon2Parameters params = new Argon2Parameters.Builder(TYPE)
				.withVersion(VERSION)
				.withMemoryAsKB(MEMORY_SIZE)
				.withIterations(ITERATIONS)
				.withParallelism(PARALLELISM)
				.withSalt(salt)
				.build();
		
		Argon2BytesGenerator generator = new Argon2BytesGenerator();
		generator.init(params);
		
		System.out.println(generator.toString());
		System.out.println(salt.toString());
		
		String encodedSalt = Base64.getEncoder().encodeToString(salt);
		System.out.println(encodedSalt);
		
		return null;
		
		
		
	}
	
	public static String generateHash(String password, String salt) {
		return null;
	}
	
	public static void main(String[] args) {
		generateHash("idk");
	}

}
