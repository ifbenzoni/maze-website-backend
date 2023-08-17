package isaiah.maze_website.converters;

import java.util.List;

import jakarta.persistence.AttributeConverter;

import com.google.gson.Gson;

import org.json.JSONArray;

/**
 * Used to convert users' saved mazes to format easier to store.
 * 
 * @author Isaiah
 *
 */
public class ConverterListIntArr2D implements AttributeConverter<List<int[][]>, String> {

	/**
	 * Converts list of mazes to string in JSON format.
	 */
	@Override
	public String convertToDatabaseColumn(List<int[][]> attribute) {
		if (attribute == null) {
			return null;
		}

		return new Gson().toJson(attribute);
	}

	// TODO: test this
	/**
	 * Converts JSON of saved mazes back to list. Uses type cast for simplicity
	 * although not sure this is acceptable for type safety.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<int[][]> convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		} else {
			JSONArray ja = new JSONArray(dbData);
			return (List<int[][]>) (Object) ja.toList();
		}
	}

}
