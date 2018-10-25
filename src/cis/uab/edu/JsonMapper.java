package cis.uab.edu;

import java.util.HashMap;

public class JsonMapper
{
	HashMap<String, Object> map = new HashMap<>();
	
	public void putString(String key, String value)
	{
		map.put(key, value);
	}
	
	public void putInt(String key, Integer value)
	{
		map.put(key, value);
	}
	
	public void putDouble(String key, Double value)
	{
		map.put(key, value);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		int keyIndex = 0;
		for (String key: map.keySet())
		{
			builder.append("\"").append(key).append("\"").append(": ");
			Object using = map.get(key);
			
			if (using.getClass() == String.class)
			{
				builder.append("\"").append(using).append("\"");
			}
			else
			{
				builder.append(using.toString());
			}
			
			if (keyIndex != map.keySet().size() - 1)
			{
				builder.append(",");
			}
			keyIndex += 1;
		}
		
		builder.append("}");
		return builder.toString();
	}
}
