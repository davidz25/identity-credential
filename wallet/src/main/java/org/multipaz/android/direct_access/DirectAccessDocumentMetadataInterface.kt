package org.multipaz.android.direct_access

import android.os.Build
import androidx.annotation.RequiresApi
import org.multipaz.document.DocumentMetadataInterface

/**
 * An interface that must be implemented by [DocumentMetadataInterface] implementation of the documents
 * that can host [DirectAccessCredential]s.
 */
@RequiresApi(Build.VERSION_CODES.P)
interface DirectAccessDocumentMetadataInterface: DocumentMetadataInterface {
    var directAccessDocumentSlot: Int

    override suspend fun documentDeleted() {
        if (directAccessDocumentSlot >= 0) {
            DirectAccess.clearDocumentSlot(directAccessDocumentSlot)
        }
    }
}