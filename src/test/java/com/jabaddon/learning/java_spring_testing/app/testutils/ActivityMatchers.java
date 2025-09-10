package com.jabaddon.learning.java_spring_testing.app.testutils;

import com.jabaddon.learning.java_spring_testing.app.domain.models.Activity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDate;

public class ActivityMatchers {

    public static Matcher<Activity> hasId(Long expectedId) {
        return new TypeSafeMatcher<>() {

            // matchesSafely is called to check if the actual Activity matches the expected criteria
            @Override
            protected boolean matchesSafely(Activity activity) {
                return expectedId.equals(activity.getId());
            }

            // describeTo is used to describe the expected condition
            @Override
            public void describeTo(Description description) {
                description.appendText("an Activity with id ").appendValue(expectedId);
            }

            // describeMismatchSafely is used to describe how the actual Activity did not match the expected condition
            @Override
            protected void describeMismatchSafely(Activity activity, Description mismatchDescription) {
                mismatchDescription.appendText("was an Activity with id ").appendValue(activity.getId());
            }
        };
    }

    public static Matcher<Activity> hasName(String expectedName) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Activity activity) {
                return expectedName.equals(activity.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an Activity with name ").appendValue(expectedName);
            }

            @Override
            protected void describeMismatchSafely(Activity activity, Description mismatchDescription) {
                mismatchDescription.appendText("was an Activity with name ").appendValue(activity.getName());
            }
        };
    }

    public static Matcher<Activity> hasMinutes(long expectedMinutes) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Activity activity) {
                return expectedMinutes == activity.getMinutes();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an Activity with minutes ").appendValue(expectedMinutes);
            }

            @Override
            protected void describeMismatchSafely(Activity activity, Description mismatchDescription) {
                mismatchDescription.appendText("was an Activity with minutes ").appendValue(activity.getMinutes());
            }
        };
    }

    public static Matcher<Activity> hasDate(LocalDate expectedDate) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Activity activity) {
                return expectedDate.equals(activity.getDate());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an activity with date ").appendValue(expectedDate);
            }

            @Override
            protected void describeMismatchSafely(Activity activity, Description mismatchDescription) {
                mismatchDescription.appendText("was an activity with date ").appendValue(activity.getDate());
            }
        };
    }

    public static Matcher<Activity> isActivityWith(Long expectedId, String expectedName, long expectedMinutes) {
        return new TypeSafeMatcher<Activity>() {
            @Override
            protected boolean matchesSafely(Activity activity) {
                return expectedId.equals(activity.getId()) &&
                       expectedName.equals(activity.getName()) &&
                       expectedMinutes == activity.getMinutes();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an activity with ")
                          .appendText("id=").appendValue(expectedId)
                          .appendText(", name=").appendValue(expectedName)
                          .appendText(", minutes=").appendValue(expectedMinutes);
            }

            @Override
            protected void describeMismatchSafely(Activity activity, Description mismatchDescription) {
                mismatchDescription.appendText("was an activity with ");

                // Build a detailed mismatch description showing only what differs
                boolean first = true;

                if (!expectedId.equals(activity.getId())) {
                    if (!first) mismatchDescription.appendText(", ");
                    mismatchDescription.appendText("id=").appendValue(activity.getId());
                    first = false;
                }

                if (!expectedName.equals(activity.getName())) {
                    if (!first) mismatchDescription.appendText(", ");
                    mismatchDescription.appendText("name=").appendValue(activity.getName());
                    first = false;
                }

                if (expectedMinutes != activity.getMinutes()) {
                    if (!first) mismatchDescription.appendText(", ");
                    mismatchDescription.appendText("minutes=").appendValue(activity.getMinutes());
                    first = false;
                }

                // If everything matches (shouldn't happen), show all values
                if (first) {
                    mismatchDescription.appendText("id=").appendValue(activity.getId())
                                     .appendText(", name=").appendValue(activity.getName())
                                     .appendText(", minutes=").appendValue(activity.getMinutes());
                }
            }
        };
    }
}
