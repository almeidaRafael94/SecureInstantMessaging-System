import java.io.OutputStream;
import java.util.concurrent.ConcurrentSkipListSet;
import com.google.gson.*;

class ServerControl {
ConcurrentSkipListSet<ClientDescription> clients = null;

ServerControl ()
{
    clients = new ConcurrentSkipListSet<ClientDescription>();
}

synchronized boolean
clientExists ( String id )
{
    return clients.contains( new ClientDescription( id, null, null ) );
}

synchronized JsonElement
getClient ( String id )
{
    for (ClientDescription c: clients) {
        if (c.id.equals( id )) {
	    return c.description;
	}
    }

    return null;
}

synchronized String
getClientLevel (String level)
{
	String a = null;
    for (ClientDescription c: clients) 
    {
    	int l = Integer.parseInt(c.description.getAsJsonObject().get("data").getAsString());
    		if(l <= Integer.parseInt(level))
    			 if (a == null) {
    				a = c.description.toString(); 
    			 }
    			 else 
    			 {
    				 a += "," + c.description; 
    			 }
	}
    return a;
}

synchronized ClientDescription
addClient ( String id, JsonElement description, OutputStream out )
{
    System.out.println ( "Added client \"" + id + "\": " + description );
    ClientDescription client = new ClientDescription( id, description, out );
    clients.add( client );
    return client;
}

synchronized boolean
removeClient ( String id )
{
    System.out.println ( "Removed client \"" + id + "\"" );
    return clients.remove( new ClientDescription( id, null, null ) );
}

synchronized String
listClients ( String id, String level)
{
	if(id.equals("null"))
		id = null;
	if(level.equals("null"))
		level = null;
	
	System.out.println("ID: " + id  + " level: " + level);
    if (id == null) {
	System.out.println( "Looking for all connected clients" );
    }
    else {
	System.out.println( "Looking for \"" + id + "\"" );
    }
    if (level == null) {
    System.out.println( "Looking for all level numbers" );
    }
    else {
    System.out.println( "Looking for levels with value less or equal than "  + level);
    }
   
    if (id != null && level == null) 
    {
    	JsonElement client = getClient( id );
			if (client != null) 
			{
			    return "[" + client + "]";
			}
		return null;
    }	
    else if (id == null && level != null)
    {
    	String client = getClientLevel( level );
		if (client != null) 
		{
		    return "[" + client + "]";
		}
		return null;
    }
    else {
	String list = null;
	for (ClientDescription c: clients) {
	    if (list == null) {
		list = "[" + c.description; 
	    }
	    else {
		list += "," + c.description; 
	    }
	}
	
	if (list == null) {
	    list = "[]";
	}
	else {
	    list += "]";
	}
        return list;
    }
}

synchronized OutputStream
getClientStream ( String id )
{
    for (ClientDescription c: clients) {
        if (c.id.equals( id )) {
	    return c.out;
	}
    }

    return null;
}

synchronized OutputStream
getOutputStream ( String id )
{
    System.out.println( "Looking for \"" + id + "\"" );

    return getClientStream( id );
}

}
