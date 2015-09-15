package machinosoccerweb.google;

import org.simpleframework.xml.*;

@Root(strict = false)
@Namespace(reference="http://www.w3.org/2005/Atom")
public class Entry {

    @Path("id[1]")
    @Text(required = false)
    private String photoUri;

    @Element(required = false)
    private String title;

    @Namespace(prefix = "gphoto", reference = "http://schemas.google.com/photos/2007")
    @Path("id[2]")
    @Text(required = false)
    private String photoId;

    @Element(required = false)
    @Namespace(prefix = "media", reference = "http://search.yahoo.com/mrss/")
    private Group group;

    public void setTags(String tags) {
        if(this.group==null) {
            this.group = new Group();
        }
        this.group.setTags(tags);
    }

    public String getPhotoURI() {
        return this.photoUri;
    }

    public String getPhotoId() {
        return this.photoId;
    }

    public String getFileName() {
        return this.title;
    }

    public String getTags() {
        return this.group.getTags();
    }

    @Root(strict = false)
    @Namespace(reference = "http://search.yahoo.com/mrss/")
    public static class Group {
        @Element(required = false)
        private String keywords;

        public String getTags() {
            return keywords;
        }

        public void setTags(String tags) {
            this.keywords = tags;
        }
    }
}
