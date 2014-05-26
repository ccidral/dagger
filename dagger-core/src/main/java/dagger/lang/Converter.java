package dagger.lang;

public interface Converter<SOURCE, TARGET> {

    TARGET convert(SOURCE source);

}
