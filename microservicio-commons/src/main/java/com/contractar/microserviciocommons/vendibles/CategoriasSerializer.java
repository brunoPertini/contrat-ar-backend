package com.contractar.microserviciocommons.vendibles;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import org.springframework.boot.jackson.JsonComponent;

import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.VendibleCategoryDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonComponent
public class CategoriasSerializer extends StdSerializer<CategoryHierarchy> {

	private static final long serialVersionUID = 4455904516043756091L;

	public CategoriasSerializer() {
		this(null);
	}

	protected CategoriasSerializer(Class<CategoryHierarchy> clazz) {
		super(clazz);
	}

	private void writeRootData(CategoryHierarchy hierachy, JsonGenerator jsonGenerator) throws IOException {
		jsonGenerator.writeStringField("root", hierachy.getRoot().getName());
	}

	private void writeChildren(JsonGenerator jsonGenerator, boolean isEmpty) throws IOException {
		jsonGenerator.writeArrayFieldStart("children");
		if (isEmpty) {
			jsonGenerator.writeEndArray();
		}
	}


	private void putChildrenInStack(Stack<CategoryHierarchy> nodesToVisit, CategoryHierarchy value,
			JsonGenerator jsonGenerator) throws IOException {
		value.getChildren().forEach(children -> {
			nodesToVisit.push(children);
		});
	}

	@Override
	public void serialize(CategoryHierarchy value, JsonGenerator jsonGenerator, SerializerProvider provider)
			throws IOException {
		Stack<Long> processingParentIds = new Stack<Long>();
		Stack<CategoryHierarchy> nodesToVisit = new Stack<CategoryHierarchy>();

		nodesToVisit.push(value);

		while (!nodesToVisit.isEmpty()) {
			CategoryHierarchy currentNode = nodesToVisit.pop();
			if(processingParentIds.isEmpty() || !processingParentIds.contains(currentNode.getRoot().getParentId())) {
				processingParentIds.push(currentNode.getRoot().getParentId());
			}
			jsonGenerator.writeStartObject();
			this.writeRootData(currentNode, jsonGenerator);
			this.putChildrenInStack(nodesToVisit, currentNode, jsonGenerator);
			boolean isLeafNode = currentNode.getChildren().size() == 0;
			if (isLeafNode) {
				this.writeChildren(jsonGenerator, true);
				jsonGenerator.writeEndObject();
			}
			boolean isStackEmpty = nodesToVisit.isEmpty();
			Long nextParentId = !isStackEmpty ? nodesToVisit.peek().getRoot().getParentId() : null;
			Long currentParentId = processingParentIds.peek();
			boolean shouldProcessNextLevel = (currentParentId == null || !(currentParentId.equals(nextParentId)));
			if (shouldProcessNextLevel) {
				boolean previousParentExists = processingParentIds.size() - 2 > 0;
				Long previousProcessedParentId = previousParentExists ? processingParentIds.elementAt(processingParentIds.size() - 2) : null;
				boolean returningToPreviousLevel = processingParentIds.contains(nextParentId);
				if (returningToPreviousLevel || isStackEmpty) {
					processingParentIds.pop();
					jsonGenerator.writeEndArray();
					jsonGenerator.writeEndObject();
				} else {	
					this.writeChildren(jsonGenerator, false);
				}
			}
		}		
	}

}
