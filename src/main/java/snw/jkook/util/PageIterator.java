package snw.jkook.util;

import java.util.Iterator;

/**
 * Represents the iterator of something can have pages. <p>
 * Default page size is <code>50</code>, you can change it by using {@link #setPageSize(int)}.
 *
 * @param <T> The type of thing will be provided
 */
public interface PageIterator<T> extends Iterator<T> {

    // we don't know, and we don't want to know if this object has more page, because it costs too many resources (e.g. Network resource)
    // so we won't support this
    /**
     * This method always fail.
     * @return Nothing will be returned
     */
    @Override
    default boolean hasNext() {
        throw new UnsupportedOperationException("hasNext");
    }

    /**
     * Get current size of the pages that iterator provides.
     */
    int getPageSize();

    /**
     * Set the size of the pages that this iterator provides.
     * @param size The size to set
     */
    void setPageSize(int size);
}
