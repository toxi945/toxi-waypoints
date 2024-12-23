package org.toxi.wpp.util;

@SuppressWarnings({"unchecked", "rawtypes"})
public interface EnumFilter<T extends Enum<T> & EnumFilter<T>> {

    default T currentEnumConstant() {
        return (T) this;
    }

    default T next() {
        return this.get(true);
    }

    default T previous() {
        return this.get(false);
    }

    private T get(boolean next) {
        try {
            int ordinal = currentEnumConstant().ordinal() + (next ? 1 : -1);
            Enum[] constants = currentEnumConstant().getClass().getEnumConstants();

            for (Enum constant : constants) {
                if (constant.ordinal() == ordinal) {
                    return (T) constant;
                }
            }

            int index = (next ? 0 : constants.length - 1);
            return (T) constants[index];
        } catch (Exception ignored) {
            return currentEnumConstant();
        }
    }
}
