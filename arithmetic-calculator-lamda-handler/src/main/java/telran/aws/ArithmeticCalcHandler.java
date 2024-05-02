package telran.aws;

import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class ArithmeticCalcHandler implements RequestStreamHandler {

	private static final String OP1 = "op1";
	private static final Object OP2 = "op2";
	private static final Object OPERATOR = "operator";
	@SuppressWarnings("unchecked")
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		BufferedReader reader=new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		String response = null;
		Map<String,Object> inputMap=null;
		LambdaLogger logger=context.getLogger();
		try {
			inputMap=(Map<String, Object>) parser.parse(reader);
			Map<String,Object>parametersMap=(Map<String, Object>) inputMap.get("pathParameters");
			if(parametersMap==null) {
				throw new Exception("No parameters Found");
			}
			String op1=(String) parametersMap.get(OP1);
			String op2=(String) parametersMap.get(OP2);
			String operator=(String) parametersMap.get(OPERATOR);
			if(op1==null||op2==null||operator==null) {
				throw new Exception("No operand or operand found");
			}
			double number1=Double.parseDouble(op1);
			double number2=Double.parseDouble(op2);
			double res = performOrparation(number1,number2,operator);
			String expression=number1+""+operator+""+number2;
			response=createNormalResponse(expression,res);
		} catch (Exception e) {
			String message=e.getMessage();
			response=createErrorResponse(message);
			logger.log("ERROR:"+message);
		}
		PrintStream printStream=new PrintStream(output);
		printStream.println(response);
		printStream.close();
		

	}
	private double performOrparation(double number1, double number2, String operator) {
		double res;
		switch (operator) {
        case "+":
            res = number1 + number2;
            break;
        case "-":
            res = number1 - number2;
            break;
        case "*":
            res = number1 * number2;
            break;
        case "/":
            if (number2 == 0) {
                throw new ArithmeticException("Division by zero");
            }
            res = number1 / number2;
            break;
        default:
            throw new IllegalArgumentException("Invalid operation: " + operator);
		}	
		return res;
	}
	private String createErrorResponse(String message) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("ErrorMessage", message);
		String dataString=JSONObject.toJSONString(data);
		map.put("body", dataString);
		map.put("statusCode", 400);
		return JSONObject.toJSONString(map);
	}
	private String createNormalResponse(String expression, double res) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> headers = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
			headers.put("Content-Type", "application/json");
			data.put("expression", expression);
			data.put("result", res);
			String dataString = JSONObject.toJSONString(data);
			map.put("body", dataString);
		map.put("headers", headers);
		map.put("statusCode", 200);
		return JSONObject.toJSONString(map);	
	}

}
