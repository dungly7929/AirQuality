/*
    Name:Dung Ly N01327929
    Course: CENG322-RND
    Purpose: Contructer for Users
    Last updated:  09 Oct 2021
*/

package ca.cheems.iotproject;

public class User
{

    private String Fullname;
    private String PhoneNo;
    private String email;
    private String password;



    public User()
    {

    }

    public User(String fullname, String phoneNo, String email, String password)
    {
        Fullname = fullname;
        PhoneNo = phoneNo;
        this.email = email;
        this.password = password;

    }

    public String getFullname()

    {
        return Fullname;
    }

    public void setFullname(String fullname)
    {
        Fullname = fullname;
    }

    public String getPhoneNo()
    {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo)
    {
        PhoneNo = phoneNo;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }



    @Override
    public String toString()
    {

        return String.format("{fullname: %s}, {password: %s}",getFullname(),getPassword());
    }

}
