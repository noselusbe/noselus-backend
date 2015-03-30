package be.noselus.model;

public class Link {
    private final String href;
    private String rel;

    public Link(final String href) {
        this.href = href;
    }

    public String getHref(){
        return href;
    }

    public String getRel(){
        return rel;
    }

    public void setRel(String rel){
        this.rel = rel;
    }
}
