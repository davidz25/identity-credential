package org.multipaz.document

import kotlinx.io.bytestring.ByteString

/**
 * Interface that all objects returned in [Document.metadata] must implement.
 *
 * Most applications will likely just use [DocumentMetadata] but if there are needs to store
 * application-specific data for each document they may implement this interface by
 * an application specific class.
 */
interface DocumentMetadataInterface {
    /** Whether the document is provisioned, i.e. issuer is ready to provide credentials. */
    val provisioned: Boolean

    /** User-facing name of this specific [Document] instance, e.g. "John's Passport". */
    val displayName: String?

    /** User-facing name of this document type, e.g. "Utopia Passport". */
    val typeDisplayName: String?

    /**
     * An image that represents this document to the user in the UI. Generally, the aspect
     * ratio of 1.586 is expected (based on ID-1 from the ISO/IEC 7810). PNG format is expected
     * and transparency is supported.
     * */
    val cardArt: ByteString?

    /**
     * An image that represents the issuer of the document in the UI, e.g. passport office logo.
     * PNG format is expected, transparency is supported and square aspect ratio is preferred.
     */
    val issuerLogo: ByteString?

    /**
     * Additional data the application wishes to store.
     */
    val other: ByteString?

    /**
     * Marks the document as being provisioned.
     *
     * This sets the [provisioned] property to `true`.
     */
    suspend fun markAsProvisioned()

    /**
     * Updates the metadata for the document.
     *
     * @param displayName User-facing name of this specific [Document] instance, e.g. "John's Passport", or `null`.
     * @param typeDisplayName User-facing name of this document type, e.g. "Utopia Passport", or `null`.
     * @param cardArt An image that represents this document to the user in the UI. Generally, the aspect
     *   ratio of 1.586 is expected (based on ID-1 from the ISO/IEC 7810). PNG format is expected
     *   and transparency is supported.
     * @param issuerLogo An image that represents the issuer of the document in the UI, e.g. passport office logo.
     *   PNG format is expected, transparency is supported and square aspect ratio is preferred.
     * @param other Additional data the application wishes to store.
     */
    suspend fun setMetadata(
        displayName: String?,
        typeDisplayName: String?,
        cardArt: ByteString?,
        issuerLogo: ByteString?,
        other: ByteString?
    )
}

/**
 * Function that creates an instance of [DocumentMetadataInterface].
 *
 * - `documentId` is [Document.identifier] for which [DocumentMetadataInterface] is created
 * - `data` is data saved by the previously-existing [DocumentMetadataInterface] for this document
 * - `saveFn` is a function that saves the state of this [DocumentMetadataInterface] instance to the
 *    persistent storage
 *
 * There are two scenarios when [DocumentMetadataInterface] is created.
 *
 * The first scenario is when a new [Document] is created using [DocumentStore.createDocument].
 * In this case, `data` is `null`. Once [DocumentMetadataInterface] is created, it is initialized using
 * function passed to [DocumentStore.createDocument] method.
 *
 * The second scenario is when a previously-existing [Document] is loaded from the storage. In this
 * case `data` is equal to the byte string that was last saved by the previously-existing
 * [DocumentMetadataInterface] for this document. If no data was saved, `data` is set to the empty byte
 * string.
 *
 * When the state of the [DocumentMetadataInterface] instance changes, it should call `saveFn` and
 * save its state to the persistent storage, so it can be loaded in the future, however exact
 * timing of `saveFn` call is entirely up to the application.
 *
 * Additionally, every time `saveFn` is called, a [DocumentUpdated] event is emitted on
 * [DocumentStore.eventFlow].
 */
typealias DocumentMetadataFactory = suspend (
    documentId: String,
    data: ByteString?,
    saveFn: suspend (data: ByteString) -> Unit
) -> DocumentMetadataInterface