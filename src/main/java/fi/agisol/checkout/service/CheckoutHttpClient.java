package fi.agisol.checkout.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckoutHttpClient {

	private final Logger log = LoggerFactory.getLogger(CheckoutHttpClient.class);

	public InputStream postRequestAndGetResponseBody(CheckoutRequestBase checkoutRequest) {
		
		InputStream inputStream = null;
		String requestUri = "";
		try {
			requestUri = checkoutRequest.getUri().toString();
			log.debug("Sending HTTP-post request ({}) to {}", checkoutRequest.getClass().getName(), requestUri );
			
			HttpResponse response = getResponseFromCheckout(checkoutRequest);

			log.debug("Received response with status '{} - {}'", response.getStatusLine().getStatusCode(),
					response.getStatusLine().getReasonPhrase());
			
			if (response.getEntity() != null) {
				inputStream = response.getEntity().getContent();
				
				// For debug purpose
				//String asString = getStringFromInputStream(inputStream);
				//inputStream.reset();
				//copyInputStreamToFile(inputStream, new File("./CheckRequestResponse.xml"));
			}


			 
		} catch (Exception e) {
			log.error("Error getting checkoutRequest from Checkout (URI: " + requestUri + ") !", e);
		}
		return inputStream;
	}

	private HttpResponse getResponseFromCheckout(CheckoutRequestBase checkoutRequest)
			throws URISyntaxException, IOException {

		URI uri = checkoutRequest.getUri();
		String postContent = checkoutRequest.getPostContent();
		HttpEntity entity = new ByteArrayEntity(postContent.getBytes());

		HttpPost post = new HttpPost(uri);
		post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		post.setEntity(entity);

		HttpClient client = HttpClientBuilder.create().build();
		return client.execute(post);

	}

// For debug purpose
//	private static void copyInputStreamToFile( InputStream in, File file ) {
//	    try {
//	        OutputStream out = new FileOutputStream(file);
//	        byte[] buf = new byte[1024];
//	        int len;
//	        while((len=in.read(buf))>0){
//	            out.write(buf,0,len);
//	        }
//	        out.close();
//	        in.close();
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
//	
//	private static String getStringFromInputStream(InputStream is) {
//
//		BufferedReader br = null;
//		StringBuilder sb = new StringBuilder();
//
//		String line;
//		try {
//
//			br = new BufferedReader(new InputStreamReader(is));
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return sb.toString();
//
//	}
}
