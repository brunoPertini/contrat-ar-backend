package com.contractar.microserviciooauth.dtos;

public class Create2FaRecordResponse {
	private int codeTtl;
	private int codeDigits;

	public Create2FaRecordResponse() {
	}

	public Create2FaRecordResponse(int codeTtl, int codeDigits) {
		this.codeTtl = codeTtl;
		this.codeDigits = codeDigits;
	}

	public int getCodeTtl() {
		return codeTtl;
	}

	public void setCodeTtl(int codeTtl) {
		this.codeTtl = codeTtl;
	}

	public int getCodeDigits() {
		return codeDigits;
	}

	public void setCodeDigits(int codeDigits) {
		this.codeDigits = codeDigits;
	}

}
