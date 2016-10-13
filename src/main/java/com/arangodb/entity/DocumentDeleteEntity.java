/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.entity;

import com.arangodb.velocypack.annotations.Expose;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class DocumentDeleteEntity<T> extends DocumentEntity {

	@Expose(deserialize = false)
	private T oldDocument;

	public DocumentDeleteEntity() {
		super();
	}

	public T getOld() {
		return oldDocument;
	}

	public void setOld(final T oldDocument) {
		this.oldDocument = oldDocument;
	}
}
