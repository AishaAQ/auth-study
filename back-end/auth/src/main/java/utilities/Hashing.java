package utilities;

import java.nio.charset.StandardCharsets;
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
	
	private static final int HASH_LENGTH = 32;
	
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
		byte[] hash = new byte[HASH_LENGTH];
	    generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);
			
		return formatHashToString(hash,salt);
		
		
	}
	
	private static String formatHashToString(byte[] hash, byte[] salt) {
		
		String hashBase64 = Base64.getEncoder().encodeToString(hash);
		String saltBase64 = Base64.getEncoder().encodeToString(salt);
		
		StringBuilder hashString = new StringBuilder().append("$argon2");
		
		System.out.println(hashBase64);
		System.out.println(saltBase64);
		
		switch (TYPE) {
			case Argon2Parameters.ARGON2_d: 
				hashString.append("d");
				break;
			case Argon2Parameters.ARGON2_i:
				hashString.append("i");
				break;
			case Argon2Parameters.ARGON2_id:
				hashString.append("id");
				break;
		}
		
		hashString.append(
				String.format(
						"$v=%d$m=%d,t=%d,p=%d$%s$%s", 
						VERSION,
						MEMORY_SIZE,
						ITERATIONS,
						PARALLELISM,
						saltBase64,
						hashBase64
				)
		);
		
		return hashString.toString();	
		
	}
	
	public static String generateHash(String password, String salt) {
		return null;
	}
	
	public static void main(String[] args) {
		generateHash("idk");
	}

}
