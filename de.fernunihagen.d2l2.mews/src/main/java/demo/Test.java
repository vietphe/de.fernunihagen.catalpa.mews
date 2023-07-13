package demo;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class Test {
	public static void main(String[] args) {
		String msg = "Did you mean <suggestion>how</suggestion> or <suggestion>it is</suggestion>?";
		String msg2 = "Did you mean <suggestion>you are</suggestion>, <suggestion>yours are</suggestion> or <suggestion>your</suggestion>?";
		if(msg.contains("<suggestion>")) {
			
			System.out.println("ok");
			System.out.println(msg2.indexOf("<suggestion>"));
			System.out.println(msg2.indexOf("</suggestion>"));
			System.out.println(msg2.substring(13+12,32));
			System.out.println(msg2.lastIndexOf("<suggestion>"));
			System.out.println(msg2.lastIndexOf("</suggestion>"));
			System.out.println(msg2.substring(85+12,101));
			
		}
		int a = LevenshteinDistance.getDefaultInstance().apply("great", "grate");
		System.out.println(a);
	}
}
