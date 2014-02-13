package dagger.servlet3;

public class TestFeatureB implements ServletFeature {

    private static boolean isEnabled;

    public static boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        TestFeatureB.isEnabled = true;
    }

}
