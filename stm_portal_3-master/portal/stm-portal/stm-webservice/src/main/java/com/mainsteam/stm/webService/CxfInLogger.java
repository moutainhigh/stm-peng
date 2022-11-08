//package com.mainsteam.stm.webService;
//
//import java.io.BufferedWriter;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.io.Reader;
//import java.io.Writer;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//
//import org.apache.commons.io.output.StringBuilderWriter;
//import org.apache.cxf.common.util.StringUtils;
//import org.apache.cxf.helpers.IOUtils;
//import org.apache.cxf.helpers.XMLUtils;
//import org.apache.cxf.interceptor.Fault;
//import org.apache.cxf.interceptor.LoggingMessage;
//import org.apache.cxf.interceptor.StaxOutInterceptor;
//import org.apache.cxf.io.CachedOutputStream;
//import org.apache.cxf.io.CachedOutputStreamCallback;
////import org.apache.cxf.io.CachedWriter;
//import org.apache.cxf.io.DelegatingInputStream;
//import org.apache.cxf.message.Message;
//import org.apache.cxf.phase.AbstractPhaseInterceptor;
//import org.apache.cxf.phase.Phase;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class CxfInLogger extends AbstractPhaseInterceptor<Message> {
//    private static final Logger logger = LoggerFactory.getLogger(CxfInLogger.class);
//    protected static final String BINARY_CONTENT_MESSAGE = "--- Binary Content ---";
//    private static final List<String> BINARY_CONTENT_MEDIA_TYPES;
//    static {
//        BINARY_CONTENT_MEDIA_TYPES = new ArrayList<String>();
//        BINARY_CONTENT_MEDIA_TYPES.add("application/octet-stream");
//        BINARY_CONTENT_MEDIA_TYPES.add("image/png");
//        BINARY_CONTENT_MEDIA_TYPES.add("image/jpeg");
//        BINARY_CONTENT_MEDIA_TYPES.add("image/gif");
//    }
//    
//    protected int limit = 100 * 1024;
//    protected long threshold = -1;
//    protected PrintWriter writer;
//    protected boolean prettyLogging;
//    private boolean showBinaryContent;
//    
//    public CxfInLogger(String phase) {
//        super(phase);
//        addBefore(StaxOutInterceptor.class.getName());
//    }
//    public CxfInLogger() {
//        this(Phase.PRE_STREAM);
//    }    
//    public CxfInLogger(int lim) {
//        this();
//        limit = lim;
//    }
//
//    public void handleMessage(Message message) throws Fault {
//             logging( message);
//    }
//    
//    protected void logging( Message message) throws Fault {
//        if (message.containsKey(LoggingMessage.ID_KEY)) {
//            return;
//        }
//        String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
//        if (id == null) {
//            id = LoggingMessage.nextId();
//            message.getExchange().put(LoggingMessage.ID_KEY, id);
//        }
//        message.put(LoggingMessage.ID_KEY, id);
//        final LoggingMessage buffer 
//            = new LoggingMessage("Inbound Message\n----------------------------", id);
//
//        Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
//        if (responseCode != null) {
//            buffer.getResponseCode().append(responseCode);
//        }
//
//        String encoding = (String)message.get(Message.ENCODING);
//
//        if (encoding != null) {
//            buffer.getEncoding().append(encoding);
//        }
//        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
//        if (httpMethod != null) {
//            buffer.getHttpMethod().append(httpMethod);
//        }
//        String ct = (String)message.get(Message.CONTENT_TYPE);
//        if (ct != null) {
//            buffer.getContentType().append(ct);
//        }
//        Object headers = message.get(Message.PROTOCOL_HEADERS);
//
//        if (headers != null) {
//            buffer.getHeader().append(headers);
//        }
//        String uri = (String)message.get(Message.REQUEST_URL);
//        if (uri != null) {
//            buffer.getAddress().append(uri);
//            String query = (String)message.get(Message.QUERY_STRING);
//            if (query != null) {
//                buffer.getAddress().append("?").append(query);
//            }
//        }
//        
//        if (!isShowBinaryContent() && isBinaryContent(ct)) {
//            buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');
//            logger.info(buffer.toString());
//            return;
//        }
//        
//        InputStream is = message.getContent(InputStream.class);
//        if (is != null) {
//            CachedOutputStream bos = new CachedOutputStream();
//            if (threshold > 0) {
//                bos.setThreshold(threshold);
//            }
//            try {
//                // use the appropriate input stream and restore it later
//                InputStream bis = is instanceof DelegatingInputStream 
//                    ? ((DelegatingInputStream)is).getInputStream() : is;
//                
//                IOUtils.copyAndCloseInput(bis, bos);
//                bos.flush();
//                bis = bos.getInputStream();
//                
//                // restore the delegating input stream or the input stream
//                if (is instanceof DelegatingInputStream) {
//                    ((DelegatingInputStream)is).setInputStream(bis);
//                } else {
//                    message.setContent(InputStream.class, bis);
//                }
//
//                if (bos.getTempFile() != null) {
//                    //large thing on disk...
//                    buffer.getMessage().append("\nMessage (saved to tmp file):\n");
//                    buffer.getMessage().append("Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
//                }
//                if (bos.size() > limit) {
//                    buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
//                }
//                writePayload(buffer.getPayload(), bos, encoding, ct); 
//                    
//                bos.close();
//            } catch (Exception e) {
//                throw new Fault(e);
//            }
//        } else {
////            Reader reader = message.getContent(Reader.class);
////            if (reader != null) {
////                try {
////                    CachedWriter writer = new CachedWriter();
////                    
////                    
////                    StringBuilderWriter writer=new StringBuilderWriter();
////                    
////                    IOUtils.copyAndCloseInput(reader, writer);
////                    message.setContent(Reader.class, writer.getReader());
////                    
////                    
////                    
////                    if (writer.getTempFile() != null) {
////                        //large thing on disk...
////                        buffer.getMessage().append("\nMessage (saved to tmp file):\n");
////                        buffer.getMessage().append("Filename: " + writer.getTempFile().getAbsolutePath() + "\n");
////                    }
////                    if (writer.size() > limit) {
////                        buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
////                    }
////                    writer.writeCacheTo(buffer.getPayload(), limit);
////                } catch (Exception e) {
////                    throw new Fault(e);
////                }
////            }
//        }
//        logger.info(formatLoggingMessage(buffer));
//    }
//
//    
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
//    protected String formatLoggingMessage(LoggingMessage buffer) {
//        return buffer.toString();
//    }
//
//    class LoggingCallback implements CachedOutputStreamCallback {
//        
//        private final Message message;
//        private final OutputStream origStream;
//        private final Logger logger; //NOPMD
//        
//        public LoggingCallback(final Logger logger, final Message msg, final OutputStream os) {
//            this.logger = logger;
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
//                logger.info(formatLoggingMessage(buffer));
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
//                //ignore
//            }
//
//            logger.info(formatLoggingMessage(buffer));
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
//			Transformer serializer = XMLUtils.newTransformer(2);
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
