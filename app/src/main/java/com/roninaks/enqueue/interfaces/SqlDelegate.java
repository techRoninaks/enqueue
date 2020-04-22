package com.roninaks.enqueue.interfaces;

import com.roninaks.enqueue.helpers.SqlHelper;

/**
 * Creates Response
 */

public interface SqlDelegate {
    void onResponse(SqlHelper sqlHelper);
}
