import java.sql.ResultSet;
import java.sql.SQLException;

public class DBTest {
	public static void main(String[] args)
	{
		DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost:3306/javaserver", "root", "");
		
		try
		{
			ResultSet rs = db.query("SELECT * FROM serverlogins");
			
			while(rs.next())
			{
				System.out.println(rs.getString("username") + " " + rs.getString("password"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
