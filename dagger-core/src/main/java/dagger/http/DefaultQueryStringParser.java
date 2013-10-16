package dagger.http;

public class DefaultQueryStringParser implements QueryStringParser {

    @Override
    public QueryString parse(String text) {
        return new QueryStringImpl(text);
    }

}
