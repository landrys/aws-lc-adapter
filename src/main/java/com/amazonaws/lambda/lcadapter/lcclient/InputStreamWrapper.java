package com.amazonaws.lambda.lcadapter.lcclient;

import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

// Class used to wrap input stream so that we know if it is
// an error input stream
public class InputStreamWrapper {
    private int responseCode = -1;
    private InputStream is; 
    private boolean errorStream = false;
    private boolean tooManyRequestsError = false;
	private HttpsURLConnection urlConnection;

    public InputStreamWrapper() {}

        /**
         * Gets the responseCode for this instance.
         *
         * @return The responseCode.
         */
        public int getResponseCode()
        {
                return this.responseCode;
        }

        /**
         * Sets the responseCode for this instance.
         *
         * @param responseCode The responseCode.
         */
        public void setResponseCode(int responseCode)
        {
                this.responseCode = responseCode;
        }

                /**
                 * Gets the is for this instance.
                 *
                 * @return The is.
                 */
                public InputStream getIs()
                {
                        return this.is;
                }

                /**
                 * Sets the is for this instance.
                 *
                 * @param is The is.
                 */
                public void setIs(InputStream is)
                {
                        this.is = is;
                }
                public void setErrorStream( boolean errorStream ) {
                    this.errorStream = errorStream;
                }
                public boolean isErrorStream() {
                    return errorStream;
                }
                public void setTooManyRequestsError( boolean tooManyRequestsError ) {
                    this.tooManyRequestsError = tooManyRequestsError;
                }
                public boolean isTooManyRequestsError() {
                    return tooManyRequestsError;
                }

				public void setUrlConnection( HttpsURLConnection uc )
				{
					this.urlConnection = uc;
				}

				public HttpsURLConnection getUrlConnection()
				{
					return this.urlConnection;
				}

}

