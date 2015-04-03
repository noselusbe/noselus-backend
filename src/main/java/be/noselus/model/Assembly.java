package be.noselus.model;

import com.google.common.base.Objects;

public class Assembly {
    private static final int WALLOON_PARLIAMENT_ID = 1;

    Link getLinkToQuestion(Question question) {
        if (getId() == WALLOON_PARLIAMENT_ID) {
            Link link = new Link("http://www.parlement-wallon.be/pwpages?p=interp-questions-voir&type=28&id_doc=" +
                    question.assemblyRef);
            link.setRel("original");
            return link;
        }
        return null;
    }

    public enum Level {
        DEPUTY_CHAMBER,
        SENAT,
        REGION,
        LOCAL,
        PROVINCE,
        EUROPE,
        FEDERAL,
        COMMUNITY
    }

    private final int id;
    private final String label;
    private final Level level;

    public Assembly(int id, String label, Level level) {
        this.id = id;
        this.label = label;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, label, level);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Assembly other = (Assembly) obj;
        return Objects.equal(this.id, other.id) && Objects.equal(this.label, other.label) && Objects.equal(this.level, other.level);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("label", label)
                .add("level", level).toString();
    }
}
