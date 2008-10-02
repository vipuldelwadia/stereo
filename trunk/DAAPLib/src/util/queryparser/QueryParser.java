package util.queryparser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.pattern.Patterns;

public class QueryParser {

	private enum BinaryOperator implements Binary<Filter> {
		AND {
			public Filter map(Filter a, Filter b) {
				return new And(a, b);
			}
		},
		OR {
			public Filter map(Filter a, Filter b) {
				return new Or(a, b);
			}
		}
	}

	public static final Parser<String> SINGLE_QUOTE_STRING = 
		      Scanners.pattern(Patterns.regex("((\\\\.)|[^\'\\\\])*") ,"quoted string").between(
		    		  Scanners.isChar('\''),
		    		  Scanners.isChar('\'')
		      ).source();
	public static final Map<String, String> SINGLE_QUOTE_STRING_MAP = new Map<String, String>() {
	    public String map(String text) {      
	        return text.substring(1, text.length()-1).replace("\\'", "'");
	    }
	    @Override public String toString() {
	        return "SINGLE_QUOTE_STRING";
	    }
	};
	
	private static final Parser<Filter> TOKEN = Terminals.StringLiteral.PARSER.map(new org.codehaus.jparsec.functors.Map<String, Filter>() {
		public Token map(String s) {
			return new Token(s);
		}
	});

	private static final Terminals OPERATORS = Terminals.operators("+", ",", "(", ")");

	@SuppressWarnings("unchecked")
	private static final Parser<String> TOKENIZER =
		Parsers.or(SINGLE_QUOTE_STRING.map(SINGLE_QUOTE_STRING_MAP), (Parser<String>)OPERATORS.tokenizer());

	private static Parser<?> term(String... names) {
		return OPERATORS.token(names);
	}

	private static <T> Parser<T> op(String name, T value) {
		return term(name).retn(value);
	}

	private static Parser<Filter> query(Parser<Filter> atom) {
		Parser.Reference<Filter> ref = Parser.newReference();
		Parser<Filter> unit = ref.lazy().between(term("("), term(")")).or(atom);
		Parser<Filter> parser = new OperatorTable<Filter>()
		.infixl(op(",", BinaryOperator.OR), 10)
		.infixl(op("+", BinaryOperator.AND), 20)
		.build(unit);
		ref.set(parser);
		return parser;
	}
	
	private static Parser<Void> nodelim = Parsers.always();
	
	private static final Parser<Filter> parser = query(TOKEN).from(TOKENIZER, nodelim);
	
	public static Filter parse(String source) {
		source = source.replace("+", "%2B");
		try {
			source = URLDecoder.decode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(source);
		return parser.parse(source);
	}
	
	public static void main(String args[]) {
		//System.out.println(SINGLE_QUOTE_STRING.parse("'foo'"));
		System.out.println(QueryParser.parse("'hi: bye'+'ho!:bo+ o\\\'n'"));
	}
}