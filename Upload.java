package ensias.um5.com.event_gl2;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName ;
    private String  mImageUrl ;
    private String place ;
    private String time ;
    private  String email ;
    private  String type ;
    private String mKey ;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Exclude
    public String getmKey() {
        return mKey;

    }
    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public  Upload () {

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Upload (String name, String mImageUrl,String time,String place,String email,String type ){

        if(name.trim().equals("")){
            name ="No Name";

        }
        mName = name ;
        this.mImageUrl = mImageUrl ;
        this.email = email ;
        this.place = place;
        this.time = time ;
        this.type = type ;

    }
}
