package com.chcgp.hpad.util.network;


public class UUIDHexGenerator extends AbstractUUIDGenerator {
	
	/**
	 * for Android, if generate in UI thread, the first 8 digits will be 0
	 * due to network on main thread exception
	 * @return
	 */
	public String generate() {
        return new StringBuffer( 36 )
                .append( format( getIP() ) )
                .append( format( getJVM() ) )
                .append( format( getHiTime() ) )
                .append( format( getLoTime() ) )
                .append( format( getCount() ) )
                .toString();
    }
	
	protected String format(int intValue) {
        String formatted = Integer.toHexString( intValue );
        StringBuffer buf = new StringBuffer( "00000000" );
        buf.replace( 8 - formatted.length(), 8, formatted );
        return buf.toString();
    }

    protected String format(short shortValue) {
        String formatted = Integer.toHexString( shortValue );
        StringBuffer buf = new StringBuffer( "0000" );
        buf.replace( 4 - formatted.length(), 4, formatted );
        return buf.toString();
    }
}
