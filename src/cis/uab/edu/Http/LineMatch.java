package cis.uab.edu.Http;

/**
 * Interface to be used for lambda arguments into HttpLineMatcher
 */
public interface LineMatch {
	void onMatch(String line);
}
