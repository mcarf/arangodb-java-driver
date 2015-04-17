/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arangodb.impl;

import java.util.Collections;
import java.util.Map;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.DocumentCursor;
import com.arangodb.InternalCursorDocumentDriver;
import com.arangodb.entity.DocumentCursorEntity;
import com.arangodb.entity.DefaultEntity;
import com.arangodb.entity.EntityFactory;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @author a-brandt
 *
 */
public class InternalCursorDocumentDriverImpl extends BaseArangoDriverImpl implements InternalCursorDocumentDriver {

	InternalCursorDocumentDriverImpl(ArangoConfigure configure, HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public DocumentCursorEntity<?> validateQuery(String database, String query) throws ArangoException {

		HttpResponseEntity res = httpManager.doPost(createEndpointUrl(database, "/_api/query"), null,
			EntityFactory.toJsonString(new MapBuilder("query", query).get()));
		// try {
		DocumentCursorEntity<?> entity = createEntity(res, DocumentCursorEntity.class);
		return entity;
		// } catch (ArangoException e) {
		// return (CursorEntity<?>) e.getEntity();
		// }

	}

	// ※Iteratorで綺麗に何回もRoundtripもしてくれる処理はClientのレイヤーで行う。
	// ※ここでは単純にコールするだけ

	@Override
	public <T> DocumentCursorEntity<T> executeQuery(
		String database,
		String query,
		Map<String, Object> bindVars,
		Class<T> clazz,
		Boolean calcCount,
		Integer batchSize,
		Boolean fullCount) throws ArangoException {

		HttpResponseEntity res = httpManager.doPost(
			createEndpointUrl(database, "/_api/cursor"),
			null,
			EntityFactory.toJsonString(new MapBuilder().put("query", query)
					.put("bindVars", bindVars == null ? Collections.emptyMap() : bindVars).put("count", calcCount)
					.put("batchSize", batchSize).put("options", new MapBuilder().put("fullCount", fullCount).get())
					.get()));
		try {
			@SuppressWarnings("unchecked")
			DocumentCursorEntity<T> entity = createEntity(res, DocumentCursorEntity.class, clazz);
			// resultを処理する
			// EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (ArangoException e) {
			throw e;
		}

	}

	// ※Iteratorで綺麗に何回もRoundtripもしてくれる処理はClientのレイヤーで行う。
	// ※ここでは単純にコールするだけ

	@Override
	public <T> DocumentCursorEntity<T> executeQuery(
		String database,
		String query,
		Map<String, Object> bindVars,
		Class<T> clazz,
		Boolean calcCount,
		Integer batchSize) throws ArangoException {

		return executeQuery(database, query, bindVars, clazz, calcCount, batchSize, false);

	}

	@Override
	public <T> DocumentCursorEntity<T> continueQuery(String database, long cursorId, Class<?>... clazz)
			throws ArangoException {

		HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/cursor", cursorId), null, null);

		try {
			@SuppressWarnings("unchecked")
			DocumentCursorEntity<T> entity = createEntity(res, DocumentCursorEntity.class, clazz);
			// resultを処理する
			// EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (ArangoException e) {
			throw e;
		}

	}

	@Override
	public DefaultEntity finishQuery(String database, long cursorId) throws ArangoException {
		HttpResponseEntity res = httpManager.doDelete(createEndpointUrl(database, "/_api/cursor/", cursorId), null);

		try {
			DefaultEntity entity = createEntity(res, DefaultEntity.class);
			return entity;
		} catch (ArangoException e) {
			// TODO Mode
			if (e.getErrorNumber() == 1600) {
				// 既に削除されている
				return (DefaultEntity) e.getEntity();
			}
			throw e;
		}
	}

	@Override
	public <T> DocumentCursor<T> executeQueryWithResultSet(
		String database,
		String query,
		Map<String, Object> bindVars,
		Class<T> clazz,
		Boolean calcCount,
		Integer batchSize,
		Boolean fullCount) throws ArangoException {

		DocumentCursorEntity<T> entity = executeQuery(database, query, bindVars, clazz, calcCount, batchSize, fullCount);
		DocumentCursor<T> rs = new DocumentCursor<T>(database, this, entity, clazz);
		return rs;

	}

	@Override
	public <T> DocumentCursor<T> executeQueryWithResultSet(
		String database,
		String query,
		Map<String, Object> bindVars,
		Class<T> clazz,
		Boolean calcCount,
		Integer batchSize) throws ArangoException {

		DocumentCursorEntity<T> entity = executeQuery(database, query, bindVars, clazz, calcCount, batchSize, false);
		DocumentCursor<T> rs = new DocumentCursor<T>(database, this, entity, clazz);
		return rs;

	}

}