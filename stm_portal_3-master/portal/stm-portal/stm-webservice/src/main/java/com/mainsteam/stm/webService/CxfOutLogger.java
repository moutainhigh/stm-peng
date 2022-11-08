//package com.mainsteam.stm.webService;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.List;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import org.apache.cxf.common.util.StringUtils;
//import org.apache.cxf.helpers
//import org.apache.cxf.interceptor.Fault;
//import org.apache.cxf.interceptor.LoggingMessage;
//import org.apache.cxf.interceptor.StaxOutInterceptor;
//import org.apache.cxf.io.CacheAndWriteOutputStream;
//import org.apache.cxf.io.CachedOutputStream;
//import org.apache.cxf.io.CachedOutputStreamCallback;
//import org.apache.cxf.message.Message;
//import org.apache.cxf.phase.AbstractPhaseInterceptor;
//import org.apache.cxf.phase.Phase;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class CxfOutLogger extends AbstractPhaseInterceptor<Message> {
//    private static final Logger logger = LoggerFactory.getLogger(CxfOutLogger.class);
//    private static final String LOG_SETUP = CxfOutLogger.class.getName() + ".log-setup";
//    protected static final String BINARY_CONTENT_MESSAGE = "--- Binary Content ---";
//    private static final List<String> BINARY_CONTENT_MEDIA_TYPES;
//    static {
//        BINARY_CONTENT_MEDIA_TYPES = new ArrayList<String>();
//		BINARY_CONTENT_MEDIA_TYPES.add("application/octet-stream");
//		BINARY_CONTENT_MEDIA_TYPES.add("image/png");
//		BINARY_CONTENT_MEDIA_TYPES.add("image/jpeg");
//		BINARY_CONTENT_MEDIA_TYPES.add("image/gif");
//    }
//    
//    protected int limit = 100 * 1024;
//    protected long threshold = -1;
//    protected PrintWriter writer;
//    protected boolean prettyLogging;
//    private boolean showBinaryContent;
//    
//    public CxfOutLogger(String phase) {
//        super(phase);
//        addBefore(StaxOutInterceptor.class.getName());
//    }
//    public CxfOutLogger() {
//        this(Phase.PRE_STREAM);
//    }    
//    public CxfOutLogger(int lim) {
//        this();
//        limit = lim;
//    }
//
//    public void handleMessage(Message message) throws Fault {
//        final OutputStream os = message.getContent(OutputStream.class);
////        final Writer iowriter = message.getContent(Writer.class);
//        if (os == null) {
//        	message.setContent(Writer.class, new LogWriter(message));
//            return;
//        }
//        message.put(LOG_SETUP, Boolean.TRUE);
//        final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
//        if (threshold > 0) {
//            newOut.setThreshold(threshold);
//        }
//        message.setContent(OutputStream.class, newOut);
//        newOut.registerCallback(new LoggingCallback(message, os));
//    }
//    
//    private LoggingMessage setupBuffer(Message message) {
//        String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
//        if (id == null) {
//            id = LoggingMessage.nextId();
//            message.getExchange().put(LoggingMessage.ID_KEY, id);
//        }
//        final LoggingMessage buffer 
//            = new LoggingMessage("Outbound Message\n---------------------------",
//                                 id);
//        
//        Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
//        if (responseCode != null) {
//            buffer.getResponseCode().append(responseCode);
//        }
//        
//        String encoding = (String)message.get(Message.ENCODING);
//        if (encoding != null) {
//            buffer.getEncoding().append(encoding);
//        }            
//        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
//        if (httpMethod != null) {
//            buffer.getHttpMethod().append(httpMethod);
//        }
//        String address = (String)message.get(Message.ENDPOINT_ADDRESS);
//        if (address != null) {
//            buffer.getAddress().append(address);
//        }
//        String ct = (String)message.get(Message.CONTENT_TYPE);
//        if (ct != null) {
//            buffer.getContentType().append(ct);
//        }
//        Object headers = message.get(Message.PROTOCOL_HEADERS);
//        if (headers != null) {
//            buffer.getHeader().append(headers);
//        }
//        return buffer;
//    }
//    
//
//    class LogWriter{
//        int count;
//        Message message;
//        
//        public LogWriter( Message message) {
//            this.message = message;
//        }
//        public void close() throws IOException {
//            LoggingMessage buffer = setupBuffer(message);
//            if (count >= limit) {
//                buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
//            }
//            String ct = (String)message.get(Message.CONTENT_TYPE);
//            try {
//                writePayload(buffer.getPayload(), ct); 
//            } catch (Exception ex) {
//                //ignore
//            }
//            logger.info(buffer.toString());
//        }
//    }
//    
//    class LoggingCallback implements CachedOutputStreamCallback {
//        
//        private final Message message;
//        private final OutputStream origStream;
//        
//        public LoggingCallback( final Message msg, final OutputStream os) {
//            this.message = msg;
//            this.origStream = os;
//        }
//
//        public void onFlush(CachedOutputStream cos) {  
//            
//        }
//        
//        public void onClose(CachedOutputStream cos) {
//            LoggingMessage buffer = setupBuffer(message);
//
//            String ct = (String)message.get(Message.CONTENT_TYPE);
//            if (!isShowBinaryContent() && isBinaryContent(ct)) {
//                buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');
//                logger.info(buffer.toString());
//                return;
//            }
//            
//            if (cos.getTempFile() == null) {
//                //buffer.append("Outbound Message:\n");
//                if (cos.size() > limit) {
//                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
//                }
//            } else {
//                buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
//                buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
//                if (cos.size() > limit) {
//                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
//                }
//            }
//            try {
//                String encoding = (String)message.get(Message.ENCODING);
//                writePayload(buffer.getPayload(), cos, encoding, ct); 
//            } catch (Exception ex) {
//               logger.error(ex.getMessage(),ex);
//            }
//
//            logger.info(buffer.toString());
//            try {
//                //empty out the cache
//                cos.lockOutputStream();
//                cos.resetOut(null, false);
//            } catch (Exception ex) {
//                //ignore
//            }
//            message.setContent(OutputStream.class,origStream);
//        }
//    }
//    
//    public void setShowBinaryContent(boolean showBinaryContent) {
//        this.showBinaryContent = showBinaryContent;
//    }
//    public boolean isShowBinaryContent() {
//        return showBinaryContent;
//    }
//    public boolean isBinaryContent(String contentType) {
//        return contentType != null && BINARY_CONTENT_MEDIA_TYPES.contains(contentType);
//    }
//
//	protected void writePayload(StringBuilder builder, CachedOutputStream cos,
//			String encoding, String contentType) throws Exception {
//		// Just transform the XML message when the cos has content
//		if ((contentType != null && contentType.indexOf("xml") >= 0 && contentType
//						.toLowerCase().indexOf("multipart/related") < 0)
//				&& cos.size() > 0) {
//			Transformer serializer = //XMLUtils.newTransformer(2);
//			// Setup indenting to "pretty print"
//			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//			serializer.setOutputProperty(
//					"{http://xml.apache.org/xslt}indent-amount", "2");
//
//			StringWriter swriter = new StringWriter();
//			serializer.transform(new StreamSource(cos.getInputStream()),
//					new StreamResult(swriter));
//			String result = swriter.toString();
//			if (result.length() < limit || limit == -1) {
//				builder.append(swriter.toString());
//			} else {
//				builder.append(swriter.toString().substring(0, limit));
//			}
//
//		} else {
//			if (StringUtils.isEmpty(encoding)) {
//				cos.writeCacheTo(builder, limit);
//			} else {
//				cos.writeCacheTo(builder, encoding, limit);
//			}
//
//		}
//	}
//
//	protected void writePayload(StringBuilder builder,String contentType) throws Exception {
//		// Just transform the XML message when the cos has content
//		if (contentType != null
//				&& contentType.indexOf("xml") >= 0) {
//			Transformer serializer = XMLUtils.newTransformer(2);
//			// Setup indenting to "pretty print"
//			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//			serializer.setOutputProperty(
//					"{http://xml.apache.org/xslt}indent-amount", "2");
//
//			StringWriter swriter = new StringWriter();
//			String result = swriter.toString();
//			if (result.length() < limit || limit == -1) {
//				builder.append(swriter.toString());
//			} else {
//				builder.append(swriter.toString().substring(0, limit));
//			}
//
//		}
//	}
//}
