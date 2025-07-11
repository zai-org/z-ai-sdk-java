package ai.z.openapi.service.document;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.api.knowledge.document.DocumentApi;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.knowledge.document.DocumentCreateParams;
import ai.z.openapi.service.knowledge.document.DocumentData;
import ai.z.openapi.service.knowledge.document.DocumentDataResponse;
import ai.z.openapi.service.knowledge.document.DocumentEditParams;
import ai.z.openapi.service.knowledge.document.DocumentEditResponse;
import ai.z.openapi.service.knowledge.document.DocumentObject;
import ai.z.openapi.service.knowledge.document.DocumentObjectResponse;
import ai.z.openapi.service.knowledge.document.DocumentPage;
import ai.z.openapi.service.knowledge.document.QueryDocumentRequest;
import ai.z.openapi.service.knowledge.document.QueryDocumentApiResponse;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import java.util.Date;

/**
 * Implementation of DocumentService
 */
@Slf4j
public class DocumentServiceImpl implements DocumentService {

	private final ZaiClient zAiClient;

	private final DocumentApi documentApi;

	private final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();

	public DocumentServiceImpl(ZaiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.documentApi = zAiClient.retrofit().create(DocumentApi.class);
	}

	@Override
	public DocumentObjectResponse createDocument(DocumentCreateParams request) {
		// Only one of getUploadDetail and getFilePath can exist
		if (request.getUploadDetail() != null && request.getFilePath() != null) {
			throw new RuntimeException("Only one of upload detail and file path can exist");
		}
		RequestSupplier<DocumentCreateParams, DocumentObject> supplier = (params) -> {
			// Convert DocumentCreateParams to MultipartBody
			MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
			try {
				if (params.getFilePath() != null) {
					java.io.File file = new java.io.File(params.getFilePath());
					if (!file.exists()) {
						throw new RuntimeException("file not found");
					}
					MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
							RequestBody.create(MediaType.parse("application/octet-stream"), file));
					formBodyBuilder.addPart(filePart);
				}
				if (params.getUploadDetail() != null) {
					formBodyBuilder.addFormDataPart("upload_detail", null, RequestBody.create(
							MediaType.parse("application/json"), mapper.writeValueAsString(params.getUploadDetail())));
				}
				formBodyBuilder.addFormDataPart("knowledge_id", params.getKnowledgeId());
				if (params.getSentenceSize() != null) {

					formBodyBuilder.addFormDataPart("sentence_size", String.valueOf(params.getSentenceSize()));
				}
				formBodyBuilder.addFormDataPart("purpose", params.getPurpose());

				if (params.getCustomSeparator() != null) {

					formBodyBuilder.addFormDataPart("custom_separator", null,
							RequestBody.create(MediaType.parse("application/json"),
									mapper.writeValueAsString(params.getCustomSeparator())));
				}

				if (params.getExtraJson() != null) {
					for (String s : params.getExtraJson().keySet()) {
						if (params.getExtraJson().get(s) instanceof String
								|| params.getExtraJson().get(s) instanceof Number
								|| params.getExtraJson().get(s) instanceof Boolean
								|| params.getExtraJson().get(s) instanceof Character) {
							formBodyBuilder.addFormDataPart(s, params.getExtraJson().get(s).toString());
						}
						else if (params.getExtraJson().get(s) instanceof Date) {
							Date date = (Date) params.getExtraJson().get(s);
							formBodyBuilder.addFormDataPart(s, String.valueOf(date.getTime()));
						}
						else {

							formBodyBuilder.addFormDataPart(s, null,
									RequestBody.create(MediaType.parse("application/json"),
											mapper.writeValueAsString(params.getExtraJson().get(s))));

						}

					}
				}
			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			MultipartBody multipartBody = formBodyBuilder.build();
			return documentApi.createDocument(multipartBody);
		};
		return zAiClient.executeRequest(request, supplier, DocumentObjectResponse.class);
	}

	@Override
	public DocumentEditResponse modifyDocument(DocumentEditParams request) {
		RequestSupplier<DocumentEditParams, Response<Void>> supplier = (params) -> documentApi
			.modifyDocument(params.getId(), params);
		return zAiClient.executeRequest(request, supplier, DocumentEditResponse.class);
	}

	@Override
	public DocumentEditResponse deleteDocument(String documentId) {
		DocumentEditParams params = new DocumentEditParams();
		params.setId(documentId);
		RequestSupplier<DocumentEditParams, Response<Void>> supplier = (params1) -> documentApi
			.deleteDocument(params1.getId());
		return zAiClient.executeRequest(params, supplier, DocumentEditResponse.class);
	}

	@Override
	public QueryDocumentApiResponse listDocuments(QueryDocumentRequest request) {
		RequestSupplier<QueryDocumentRequest, DocumentPage> supplier = (params) -> documentApi.queryDocumentList(
				params.getKnowledgeId(), params.getPurpose(), params.getPage(), params.getLimit(), params.getOrder());
		return zAiClient.executeRequest(request, supplier, QueryDocumentApiResponse.class);
	}

	@Override
	public DocumentDataResponse retrieveDocument(String documentId) {
		DocumentEditParams params = new DocumentEditParams();
		params.setId(documentId);
		RequestSupplier<DocumentEditParams, DocumentData> supplier = (id) -> documentApi.retrieveDocument(id.getId());
		return zAiClient.executeRequest(params, supplier, DocumentDataResponse.class);
	}

}