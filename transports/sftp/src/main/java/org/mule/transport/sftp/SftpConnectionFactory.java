
package org.mule.transport.sftp;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.ImmutableEndpoint;

import java.io.IOException;

public class SftpConnectionFactory implements PoolableObjectFactory
{
    private final static Logger logger = Logger.getLogger(SftpConnectionFactory.class);

    private final ImmutableEndpoint endpoint;

    public SftpConnectionFactory(ImmutableEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }

    public void activateObject(Object o) throws Exception
    {
        // Do nothing!
    }

    public void destroyObject(Object o) throws Exception
    {
        SftpClient client = (SftpClient) o;
        client.disconnect();
    }

    public Object makeObject() throws Exception
    {
        return createClient(endpoint);
    }

    public static SftpClient createClient(ImmutableEndpoint endpoint) throws Exception
    {
        EndpointURI endpointURI = endpoint.getEndpointURI();

        String host = endpointURI.getHost();
        if (logger.isDebugEnabled())
        {
            logger.debug("Using host: " + host);
        }

        SftpClient client = new SftpClient(host);

        try
        {
            int uriPort = endpointURI.getPort();
            if (uriPort != -1)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Using port: " + uriPort);
                }
                client.setPort(uriPort);
            }

            SftpUtil sftpUtil = new SftpUtil(endpoint);
            String identityFile = sftpUtil.getIdentityFile();

            /*
             * TODO: There is a problem if the SSHd uses a low value of
             * "MaxStartups", which means that if there is many new concurrent
             * connections the server will respond with "Connection reset", and thus
             * we will get exceptions of type
             * "Session.connect: java.net.SocketException: Connection reset"...
             * Solutions: increase the MaxStartups on the server or fix a retry or
             * use a exception-strategy(?)
             */

            // boolean succeeded = false;
            // int numberOfAttempts = 2;
            // while(!succeeded && numberOfAttempts > 0)
            // {
            // try
            // {
            if (identityFile != null)
            {
                String passphrase = sftpUtil.getPassphrase();

                client.login(endpointURI.getUser(), identityFile, passphrase);
            }
            else
            {
                client.login(endpointURI.getUser(), endpointURI.getPassword());
            }
            // } catch (SocketException e)
            // {
            // numberOfAttempts--;
            // continue;
            // }
            // succeeded = true;
            // }

            if (logger.isDebugEnabled())
            {
                logger.debug("Successfully connected to: " + endpointURI);
            }

            return client;
        }
        catch (IOException e)
        {
            client.disconnect();
            throw e;
        }
    }

    public void passivateObject(Object o) throws Exception
    {
        // Do nothing!
    }

    public boolean validateObject(Object o)
    {
        SftpClient client = (SftpClient) o;
        if (logger.isDebugEnabled())
        {
            logger.debug("Inside validateObject - will return " + client.isConnected());
        }
        return client.isConnected();
    }
}
