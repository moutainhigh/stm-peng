// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) braces fieldsfirst splitstr(80) 
// Source File Name:   AdminClient.java

package com.sun.jmx.remote.opt.security;

import com.sun.jmx.remote.generic.*;
import com.sun.jmx.remote.opt.util.ClassLogger;
import com.sun.jmx.remote.opt.util.EnvHelp;

import java.io.IOException;
import java.util.*;

import javax.management.remote.generic.MessageConnection;
import javax.management.remote.message.*;

public class AdminClient
    implements ClientAdmin
{

    @SuppressWarnings("rawtypes")
	private Map env;
    private String connectionId;
    @SuppressWarnings("rawtypes")
	private List profilesList;
    private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "AdminClient");

    @SuppressWarnings("rawtypes")
	public AdminClient(Map map)
    {
        env = null;
        connectionId = null;
        profilesList = new ArrayList();
        env = map == null ? Collections.EMPTY_MAP : map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public MessageConnection connectionOpen(MessageConnection messageconnection)
        throws IOException
    {
        boolean flag = true;
        Object obj = null;
        try
        {
            HandshakeBeginMessage handshakebeginmessage = null;
            javax.management.remote.message.Message message = messageconnection.readMessage();
            System.out.println("jmx message="+message);
            if(message instanceof HandshakeBeginMessage)
            {
                handshakebeginmessage = (HandshakeBeginMessage)message;
            } else
            if(message instanceof HandshakeErrorMessage)
            {
                flag = false;
                HandshakeErrorMessage handshakeerrormessage = (HandshakeErrorMessage)message;
                throwExceptionOnError(handshakeerrormessage);
            } else
            {
                throw new IOException("Unexpected message: " + message.getClass().getName());
            }
            String s = handshakebeginmessage.getProfiles();
            String s1 = handshakebeginmessage.getVersion();
            if(logger.traceOn())
            {
                logger.trace("connectionOpen", ">>>>> Handshake Begin <<<<<");
                logger.trace("connectionOpen", "Server Supported Profiles [ " + s + " ]");
                logger.trace("connectionOpen", "Server JMXMP Version [ " + s1 + " ]");
            }
            String s2 = "1.0";
            if(!s2.equals(s1))
            {
                if(s2.compareTo(s1) > 0)
                {
                    throw new IOException("The client is already using the lowest JMXMP protocol version [" + s2 + "]");
                }
                VersionMessage versionmessage = new VersionMessage(s2);
                messageconnection.writeMessage(versionmessage);
                message = messageconnection.readMessage();
                if(message instanceof VersionMessage)
                {
                    VersionMessage versionmessage1 = (VersionMessage)message;
                    if(!s2.equals(versionmessage1.getVersion()))
                    {
                        throw new IOException("Protocol version mismatch: Client [" + s2 + "] vs. Server [" + versionmessage1.getVersion() + "]");
                    }
                } else
                if(message instanceof HandshakeErrorMessage)
                {
                    flag = false;
                    HandshakeErrorMessage handshakeerrormessage1 = (HandshakeErrorMessage)message;
                    throwExceptionOnError(handshakeerrormessage1);
                } else
                {
                    throw new IOException("Unexpected message: " + message.getClass().getName());
                }
            }
            List list = selectProfiles(s);
            ProfileClient profileclient;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); profilesList.add(profileclient))
            {
                String s3 = (String)iterator.next();
                profileclient = ProfileClientFactory.createProfile(s3, env);
                if(logger.traceOn())
                {
                    logger.trace("connectionOpen", ">>>>> Profile " + profileclient.getClass().getName() + " <<<<<");
                }
                Object obj2 = null;
                profileclient.initialize(messageconnection);
                while(!profileclient.isComplete()) 
                {
                    ProfileMessage profilemessage = profileclient.produceMessage();
                    messageconnection.writeMessage(profilemessage);
                    message = messageconnection.readMessage();
                    if(message instanceof ProfileMessage)
                    {
                        profileclient.consumeMessage((ProfileMessage)message);
                    } else
                    if(message instanceof HandshakeErrorMessage)
                    {
                        flag = false;
                        HandshakeErrorMessage handshakeerrormessage2 = (HandshakeErrorMessage)message;
                        throwExceptionOnError(handshakeerrormessage2);
                    } else
                    {
                        throw new IOException("Unexpected message: " + message.getClass().getName());
                    }
                }
                profileclient.activate();
            }

            Object obj1 = env.get("jmx.remote.context");
            HandshakeEndMessage handshakeendmessage = new HandshakeEndMessage(obj1, null);
            if(logger.traceOn())
            {
                logger.trace("connectionOpen", ">>>>> Handshake End <<<<<");
                logger.trace("connectionOpen", "Client Context Object [ " + obj1 + " ]");
            }
            messageconnection.writeMessage(handshakeendmessage);
            HandshakeEndMessage handshakeendmessage1 = null;
            message = messageconnection.readMessage();
            if(message instanceof HandshakeEndMessage)
            {
                handshakeendmessage1 = (HandshakeEndMessage)message;
            } else
            if(message instanceof HandshakeErrorMessage)
            {
                flag = false;
                HandshakeErrorMessage handshakeerrormessage3 = (HandshakeErrorMessage)message;
                throwExceptionOnError(handshakeerrormessage3);
            } else
            {
                throw new IOException("Unexpected message: " + message.getClass().getName());
            }
            Object obj3 = handshakeendmessage1.getContext();
            connectionId = handshakeendmessage1.getConnectionId();
            if(logger.traceOn())
            {
                logger.trace("connectionOpen", "Server Context Object [ " + obj3 + " ]");
                logger.trace("connectionOpen", "Server Connection Id [ " + connectionId + " ]");
            }
        }
        catch(Exception exception)
        {
            if(flag)
            {
                try
                {
                    messageconnection.writeMessage(new HandshakeErrorMessage(exception.toString()));
                }
                catch(Exception exception1)
                {
                    if(logger.debugOn())
                    {
                        logger.debug("connectionOpen", "Could not send HandshakeErrorMessage to the server", exception1);
                    }
                }
            }
            if(exception instanceof RuntimeException)
            {
                throw (RuntimeException)exception;
            }
            if(exception instanceof IOException)
            {
                throw (IOException)exception;
            } else
            {
                throw (IOException)EnvHelp.initCause(new IOException(exception.getMessage()), exception);
            }
        }
        return messageconnection;
    }

    @SuppressWarnings("rawtypes")
	public void connectionClosed(MessageConnection messageconnection)
    {
        Iterator iterator = profilesList.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            ProfileClient profileclient = (ProfileClient)iterator.next();
            try
            {
                profileclient.terminate();
            }
            catch(Exception exception)
            {
                if(logger.debugOn())
                {
                    logger.debug("connectionClosed", "Got an exception to terminate a ProfileClient: " + profileclient.getName(), exception);
                }
            }
        } while(true);
        profilesList.clear();
    }

    public String getConnectionId()
    {
        return connectionId;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List selectProfiles(String s)
        throws Exception
    {
        SelectProfiles selectprofiles = (SelectProfiles)env.get("com.sun.jmx.remote.profile.selector");
        if(selectprofiles != null)
        {
            selectprofiles.selectProfiles(env, s);
            String s1 = (String)env.get("jmx.remote.profiles");
            if(s1 == null)
            {
                return Collections.EMPTY_LIST;
            }
            StringTokenizer stringtokenizer = new StringTokenizer(s1, " ");
            ArrayList arraylist = new ArrayList(stringtokenizer.countTokens());
            String s3;
            for(; stringtokenizer.hasMoreTokens(); arraylist.add(s3))
            {
                s3 = stringtokenizer.nextToken();
            }

            return arraylist;
        }
        String s2 = (String)env.get("jmx.remote.profiles");
        boolean flag = s == null || s.equals("");
        boolean flag1 = s2 == null || s2.equals("");
        if(flag && flag1)
        {
            return Collections.EMPTY_LIST;
        }
        if(flag)
        {
            throw new IOException("The server does not support any profile but the client requires one");
        }
        if(flag1)
        {
            throw new IOException("The client does not require any profile but the server mandates one");
        }
        StringTokenizer stringtokenizer1 = new StringTokenizer(s, " ");
        ArrayList arraylist1 = new ArrayList(stringtokenizer1.countTokens());
        String s4;
        for(; stringtokenizer1.hasMoreTokens(); arraylist1.add(s4))
        {
            s4 = stringtokenizer1.nextToken();
        }

        int i = arraylist1.size();
        StringTokenizer stringtokenizer2 = new StringTokenizer(s2, " ");
        ArrayList arraylist2 = new ArrayList(stringtokenizer2.countTokens());
        String s5;
        for(; stringtokenizer2.hasMoreTokens(); arraylist2.add(s5))
        {
            s5 = stringtokenizer2.nextToken();
        }

        int j = arraylist2.size();
        if(i < j || !arraylist1.containsAll(arraylist2))
        {
            throw new IOException("The server supported profiles " + arraylist1 + " do not " + "match the client required profiles " + arraylist2 + ".");
        } else
        {
            return arraylist2;
        }
    }

    static void throwExceptionOnError(HandshakeErrorMessage handshakeerrormessage)
        throws IOException, SecurityException
    {
        String s = handshakeerrormessage.getDetail();
        if(s.startsWith("java.lang.SecurityException") || s.startsWith("java.security.") || s.startsWith("javax.net.ssl.") || s.startsWith("javax.security."))
        {
            throw new SecurityException(s);
        } else
        {
            throw new IOException(s);
        }
    }

}
