import java.sql.*;
public class DatabaseConnection {
	private static Connection connection;
	private Statement stm;
	private ResultSet credentials;
	private String url;
	private String user;
	private String pass;
	
	public DatabaseConnection(String url, String user, String pass)
	{
		this.user = user;
		this.pass = pass;
		this.url = url;
		
		try
		{
			connection = DriverManager.getConnection(url, user, pass);
		}
		catch (SQLException e)
		{
			System.out.println("Sorry, cannot connect to this database :(");
			e.printStackTrace();
		}	
	}
	
	public void close()
	{
		try
		{
			System.out.println("Connection closed successfully.");
			this.stm.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String query)
	{
		if(query.contains(";"))
		{
			System.out.println("The query was aborted for protection from SQL Injection concerns.");
			return null;
		}
		else
		{
			query += ";";
		}
		try
		{
			this.stm = connection.createStatement();
			this.credentials = stm.executeQuery(query);
		}
		catch (SQLException e)
		{
			System.out.println("Statement execution failed...");
			e.printStackTrace();
		}
		return this.credentials;
	}
	
	public boolean authenticate(String username, String password)
	{
		boolean authenticated = false;
		PreparedStatement ps = null;
		ResultSet set = null;
		
		if(username == null || password == null)
		{
			authenticated = false;
			username = "";
			password = "";
		}
		try
		{
			ps = connection.prepareStatement("SELECT password FROM serverlogins WHERE username = ?");
			ps.setString(1, username);
			set = ps.executeQuery();
			
			if(set.next())
			{
				password = set.getString("password");
				System.out.println("User " + username + " logged in successfuly!");
				authenticated = true;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				set.close();
				ps.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return authenticated;
	}
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public Statement getStatement()
	{
		return this.stm;
	}
}
