package dagger.servlet3;

import dagger.servlet3.features.ServletFeature;

public class TestFeatureA implements ServletFeature {

    private static boolean isEnabled;

    public static boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        TestFeatureA.isEnabled = true;
    }

}
