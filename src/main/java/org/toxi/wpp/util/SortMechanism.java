package org.toxi.wpp.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.toxi.wpp.data.Waypoint;

import java.util.Comparator;

public enum SortMechanism implements Comparator<Waypoint>, EnumFilter<SortMechanism> {

    NAME_ASCENDING() {
        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.name(), o2.name());
        }
    },

    NAME_DESCENDING() {
        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            return -NAME_ASCENDING.compare(o2, o1);
        }
    },

    TIME_ASCENDING() {
        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            return Long.compare(o1.creationTime(), o2.creationTime());
        }
    },

    TIME_DESCENDING() {
        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            return -TIME_ASCENDING.compare(o2, o1);
        }
    };


    public static class SimplePriviligeComparator implements Comparator<Waypoint> {
        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            if (o1.isFavorite() && !o2.isFavorite()) {
                return -1;
            } else if (!o1.isFavorite() && o2.isFavorite()) {
                return 1;
            } else if (o1.isFavorite() && o2.isFavorite() || !o1.isFavorite() && !o2.isFavorite()) {
                return 0;
            }
            return 0;
        }
    }

    // FIXME: Find a good solution to sort by favorite first (if enabled) and then by the actual mechanism
    public static class PriviligeComparator implements Comparator<Waypoint> {
        private final SortMechanism sortMechanism;

        public PriviligeComparator(@NotNull SortMechanism sortMechanism) {
            this.sortMechanism = Preconditions.checkNotNull(sortMechanism);
        }

        @Override
        public int compare(Waypoint o1, Waypoint o2) {
            if (o1.isFavorite() && !o2.isFavorite()) {
                return -1;
            } else if (!o1.isFavorite() && o2.isFavorite()) {
                return 1;
            } else if (o1.isFavorite() && o2.isFavorite() || !o1.isFavorite() && !o2.isFavorite()) {
                return compareForMechanism(o1, o2);
            }
            return 0;
        }

        public int compareForMechanism(Waypoint o1, Waypoint o2) {
            return switch (sortMechanism) {
                case NAME_DESCENDING -> -String.CASE_INSENSITIVE_ORDER.compare(o1.name(), o2.name());
                case TIME_ASCENDING -> Long.compare(o1.creationTime(), o2.creationTime());
                case TIME_DESCENDING -> -Long.compare(o1.creationTime(), o2.creationTime());
                default -> String.CASE_INSENSITIVE_ORDER.compare(o1.name(), o2.name());
            };
        }
    }
}
