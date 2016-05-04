package shu.example;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JSONParser {

/** The characters in "true". */
private static final char[] TRUE = {'t', 'r', 'u', 'e'};
/** The characters in "false". */
private static final char[] FALSE = {'f', 'a', 'l', 's', 'e'};
/** The characters in "null". */
private static final char[] NULL = {'n', 'u', 'l', 'l'};

private final Reader reader;
private int offset;
private int ch;

public JSONParser(@NotNull Reader reader) throws IOException {
	this.reader = reader;
	offset = -1;
	read();
}

@Nullable
public Object parse() throws IOException, ParseException {
	skipWhitespace();
	return ch == -1 ? null : parseValue();
}

@Nullable
private Object parseValue() throws IOException, ParseException {
	if (ch == '{') {
		return parseObject();
	}
	if (ch == '[') {
		return parseArray();
	}
	if (ch == '"') {
		return parseString();
	}
	if (ch == '-' || ch >= '0' && ch <= '9') {
		return parseNumber();
	}
	if (ch == 't' || ch == 'f') {
		return parseBoolean();
	}
	if (ch == 'n') {
		return parseNull();
	}
	throwUnexpectedCharacter();
	return null;
}

@NotNull
private String parseString() throws IOException, ParseException {
	StringBuilder sb = new StringBuilder(16);
	read();
	while (ch != '"') {
		if (ch == '\\') {
			read();
			switch (ch) {
				case '"':
				case '\\':
				case '/':
					sb.append((char) ch);
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'u':
					int v = 0;
					for (int i = 0; i < 4; ++i) {
						read();
						if (ch == -1) {
							throw new ParseException(
									"Unexpected end of stream", offset);
						}
						v <<= 4;
						if (ch >= '0' && ch <= '9') {
							v |= ch - '0';
						}
						else if (ch >= 'A' && ch <= 'F') {
							v |= ch + 10 - 'A';
						}
						else if (ch >= 'a' && ch <= 'f') {
							v |= ch + 10 - 'a';
						}
						else {
							throwUnexpectedCharacter();
						}
					}
					sb.append((char) v);
					break;
				default:
					throwUnexpectedCharacter();
			}
		}
		else if (ch == -1) {
			throw new ParseException("Unexpected end of stream", offset);
		}
		else if (ch != '"') {
			sb.append((char) ch);
		}
		read();
	}
	read();
	return sb.toString();
}

@NotNull
private Number parseNumber() throws IOException, ParseException {
	StringBuilder sb = new StringBuilder(16);
	sb.append((char) ch);
	int state = ch == '-' ? 1 : ch == '0' ? 3 : 9;
	while (state != -1) {
		switch (state) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 9:
			case 11:
			case 13:
			case 15:
				read();
				++state;
				break;
			case 2:
				if (ch == '0') {
					sb.append((char) ch);
					state = 3;
				}
				else if (ch >= '1' && ch <= '9') {
					sb.append((char) ch);
					state = 9;
				}
				else {
					throwUnexpectedCharacter();
				}
				break;
			case 4:
				if (ch == '.') {
					sb.append((char) ch);
					state = 5;
				}
				else if (ch == 'e' || ch == 'E') {
					sb.append((char) ch);
					state = 11;
				}
				else {
					state = -1;
				}
				break;
			case 6:
				if (ch >= '0' && ch <= '9') {
					sb.append((char) ch);
					state = 7;
				}
				else {
					throwUnexpectedCharacter();
				}
				break;
			case 8:
				if (ch >= '0' && ch <= '9') {
					sb.append((char) ch);
					state = 7;
				}
				else if (ch == 'e' || ch == 'E') {
					sb.append((char) ch);
					state = 11;
				}
				else {
					state = -1;
				}
				break;
			case 10:
				if (ch >= '0' && ch <= '9') {
					sb.append((char) ch);
					state = 9;
				}
				else {
					state = 4;
				}
				break;
			case 12:
				if (ch == '+' || ch == '-') {
					sb.append((char) ch);
					state = 13;
				}
				else {
					state = 14;
				}
				break;
			case 14:
				if (ch >= '0' && ch <= '9') {
					sb.append((char) ch);
					state = 15;
				}
				else {
					throwUnexpectedCharacter();
				}
				break;
			case 16:
				if (ch >= '0' && ch <= '9') {
					sb.append((char) ch);
					state = 15;
				}
				else {
					state = -1;
				}
				break;
		}
	}
	return new Double(sb.toString());
}

@NotNull
private Boolean parseBoolean() throws IOException, ParseException {
	Boolean value = ch == 't' ? Boolean.TRUE : Boolean.FALSE;
	checkSequence(ch == 't' ? TRUE : FALSE);
	return value;
}

@Nullable
private Object parseNull() throws IOException, ParseException {
	checkSequence(NULL);
	return null;
}

@NotNull
private Map<String, Object> parseObject()
		throws IOException, ParseException {
	Map<String, Object> object = new LinkedHashMap<>();
	read();
	skipWhitespace();
	while (ch != '}') {
		if (ch != '"') {
			throwUnexpectedCharacter();
		}
		String key = parseString();
		skipWhitespace();
		if (ch != ':') {
			throwUnexpectedCharacter();
		}
		read();
		skipWhitespace();
		object.put(key, parseValue());
		skipWhitespace();
		if (ch == ',') {
			read();
			skipWhitespace();
		}
		else if (ch != '}') {
			throwUnexpectedCharacter();
		}
	}
	read();
	return object;
}

@NotNull
private List<Object> parseArray()
		throws IOException, ParseException {
	List<Object> array = new ArrayList<>();
	read();
	skipWhitespace();
	while (ch != ']') {
		array.add(parseValue());
		skipWhitespace();
		if (ch == ',') {
			read();
			skipWhitespace();
		}
		else if (ch != ']') {
			throwUnexpectedCharacter();
		}
	}
	read();
	return array;
}

private void checkSequence(char[] sequence)
		throws IOException, ParseException {
	for (int i = 1; i < sequence.length; ++i) {
		read();
		if (ch != sequence[i]) {
			throwUnexpectedCharacter();
		}
	}
	read();
}

private void throwUnexpectedCharacter() throws ParseException {
	throw new ParseException(ch == -1 ? "Unexpected end of stream" :
			"Unexpected character '" + (char) ch + "' found at " + offset,
			offset);
}

private void skipWhitespace() throws IOException {
	while (Character.isWhitespace(ch)) {
		read();
	}
}

private void read() throws IOException {
	++offset;
	ch = reader.read();
}

}
