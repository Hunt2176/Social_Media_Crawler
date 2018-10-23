package cis.uab.edu.Request;

/**
 * Interface to be used for lambda arguments into HttpLineMatcher
 */
public interface LineMatch {
	void onMatch(String line);
}
