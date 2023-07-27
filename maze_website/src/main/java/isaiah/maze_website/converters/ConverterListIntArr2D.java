package isaiah.maze_website.converters;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Type;

import jakarta.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

public class ConverterListIntArr2D implements AttributeConverter<List<int[][]>, String> {

	@Override
	public String convertToDatabaseColumn(List<int[][]> attribute) {
		if (attribute == null) {
			return null;
		}
		
		return new Gson().toJson(attribute);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<int[][]> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		} else {
			JSONArray ja = new JSONArray(dbData);
			//TODO: (not sure how to enforce typesafety) should be typesafe because converts from JSON made from List<int[][]>, note in javadoc if true
			//TODO rename class to maze steps converter for clarity
			return (List<int[][]>)(Object)ja.toList();
		}
	}

}
