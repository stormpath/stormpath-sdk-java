package com.stormpath.sdk.impl.authc.credentials

import org.testng.annotations.Test
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail


public class ClientCredentialsProviderChainTest {

    @Test
    public void credentialsReturnedInOrder() {

        ClientCredentials firstCredentials = buildClientCredentials()
        ClientCredentials secondCredentials = buildClientCredentials()

        ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return firstCredentials
            }
        }

        ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return secondCredentials
            }
        }

        ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
        chain.addClientCredentialsProviders(firstProvider, secondProvider)

        ClientCredentials returned = chain.getClientCredentials()

        assertEquals(firstCredentials.id, returned.id)
        assertEquals(firstCredentials.secret, returned.secret)
        assertNotEquals(secondCredentials.id, returned.id)
        assertNotEquals(secondCredentials.secret, returned.secret)


    }

    @Test
    public void chainSearchedUntilCredentialsFound(){
        ClientCredentials thirdCredentials = buildClientCredentials()

        ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                throw new IllegalStateException("can't find credentials")
            }
        }

        ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                throw new IllegalStateException("can't find credentials")
            }
        }

        ClientCredentialsProvider thirdProvider = new ClientCredentialsProvider() {
            @Override
            ClientCredentials getClientCredentials() {
                return thirdCredentials
            }
        }

        ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
        chain.addClientCredentialsProviders(firstProvider, secondProvider, thirdProvider)

        ClientCredentials returned = chain.getClientCredentials()

        assertEquals(thirdCredentials.id, returned.id)
        assertEquals(thirdCredentials.secret, returned.secret)

    }

    @Test
    public void emptyChainThrowsException(){
        try{
            ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
            chain.getClientCredentials()
            fail("shouldn't get here")
        }
        catch (Throwable throwable){
            assertTrue(throwable instanceof IllegalStateException)
            assertEquals(throwable.getMessage(), "Unable to load credentials from any provider in the chain.")
        }
    }

    @Test
    public void exhaustedChainThrowsException(){
        try{
            ClientCredentialsProvider firstProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProvider secondProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProvider thirdProvider = new ClientCredentialsProvider() {
                @Override
                ClientCredentials getClientCredentials() {
                    throw new IllegalStateException("can't find credentials")
                }
            }

            ClientCredentialsProviderChain chain = new ClientCredentialsProviderChain() {};
            chain.addClientCredentialsProviders(firstProvider, secondProvider, thirdProvider)
            chain.getClientCredentials()
            fail("shouldn't get here")
        }
        catch (Throwable throwable){
            assertTrue(throwable instanceof IllegalStateException)
            assertEquals(throwable.getMessage(), "Unable to load credentials from any provider in the chain.")
        }
    }



    private static ClientCredentials buildClientCredentials() {

        String id = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        return new ClientCredentials() {

            @Override
            String getId() {
                return id
            }

            @Override
            String getSecret() {
                return secret
            }
        }
    }

}
