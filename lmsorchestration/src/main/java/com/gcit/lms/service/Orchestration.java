package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
public class Orchestration {

	List<HttpMessageConverter<?>> mc = new ArrayList<HttpMessageConverter<?>>();
	RestTemplate rest = new RestTemplate();
	ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
	String adminURL = "http://localhost:1111";
	String librarianURL = "http://localhost:2222";
	String borrowerURL = "http://localhost:3333";
	String URL = "";

	public Orchestration() {
		rest = new RestTemplate(requestFactory);
		mc.add(new FormHttpMessageConverter());
		mc.add(new StringHttpMessageConverter());
		mc.add(new MappingJackson2HttpMessageConverter());
		rest.setMessageConverters(mc);
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Authors", "/Admin/Books", "/Admin/Genres", "/Admin/Publishers", "/Admin/Branches",
			"/Admin/Borrowers", "/Admin/BookLoans",
			"Librarian/Branches" }, method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	public List<Object> readAll(HttpServletRequest req) throws SQLException {
		getURL(req);
		ResponseEntity<List> st = rest.getForEntity(URL + req.getRequestURI(), List.class);
		List<Object> obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = { "Librarian/Branches/{id}/Books",
			"/Borrower/Branches/{id}/Books" }, method = RequestMethod.GET, produces = { "application/json",
					"application/xml" })
	public List<Object> readBranchBooks(@PathVariable Integer id, @RequestParam Integer pageNo, HttpServletRequest req)
			throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL + req.getRequestURI())
				.queryParam("pageNo", pageNo);
		ResponseEntity<List> st = rest.getForEntity(builder.toUriString(), List.class, params);
		List<Object> obj = st.getBody();
		return obj;
	}
	
	@Transactional
	@RequestMapping(value = {"/Borrower/Branches/{branchId}/CardNo/{cardNo}/BookLoans"}, method = RequestMethod.GET, produces = { "application/json",
					"application/xml" })
	public List<Object> readBookLoans(@PathVariable Integer branchId, @PathVariable Integer cardNo, @RequestParam Integer pageNo, HttpServletRequest req)
			throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("cardNo", "" + cardNo);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL + req.getRequestURI())
				.queryParam("pageNo", pageNo);
		ResponseEntity<List> st = rest.getForEntity(builder.toUriString(), List.class, params);
		List<Object> obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = { "Librarian/Branches/{branchId}/Books/{bookId}/BookCopies",
			"/Borrower/Branches/{branchId}/Books/{bookId}/NoOfCopies" }, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public Object readBookCopies(@PathVariable Integer branchId, @PathVariable Integer bookId, HttpServletRequest req)
			throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		ResponseEntity<Object> st = rest.getForEntity(URL + req.getRequestURI(), Object.class, params);
		Object obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Author", "/Admin/Book", "/Admin/Genre", "/Admin/Publisher", "/Admin/Branch",
			"/Admin/Borrower", "/Admin/DueDate", "/Admin/NoOfCopies",
			"Librarian/Branches/Branch" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public void save(@RequestBody Object obj, HttpServletRequest req) throws SQLException {
		getURL(req);
		HttpEntity<Object> request = new HttpEntity<>(obj);
		ResponseEntity<Object> st = rest.postForEntity(URL + req.getRequestURI(), request, Object.class);
		System.out.println(st.getBody());
	}

	// tricky
	@Transactional
	@RequestMapping(value = {
			"Librarian/Branches/{branchId}/Books/{bookId}/NoOfCopies" }, method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public void updateNoOfCopies(@PathVariable Integer branchId, @PathVariable Integer bookId, @RequestBody Object obj,
			HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		HttpEntity<Object> request = new HttpEntity<>(obj);
		ResponseEntity<Object> st = rest.postForEntity(URL + req.getRequestURI(), request, Object.class, params);
		System.out.println(st.getBody());
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Author", "/Admin/Book", "/Admin/Genre", "/Admin/Publisher", "/Admin/Branch",
			"/Admin/Borrower" }, method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public void delete(@RequestParam Integer id, HttpServletRequest req) {
		getURL(req);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL + req.getRequestURI()).queryParam("id",
				id);

		rest.delete(builder.toUriString());
	}

	@Transactional
	@RequestMapping(value = { "/Admin/authorsCount", "/Admin/booksCount", "/Admin/genresCount",
			"/Admin/publishersCount", "/Admin/branchesCount", "/Admin/borrowersCount", "/Admin/bookLoansCount",
			"Librarian/branchesCount",
			"/Borrower/branchesCount" }, method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	public Integer getCount(HttpServletRequest req) throws SQLException {
		getURL(req);
		System.out.println(URL + req.getRequestURI());
		ResponseEntity<Integer> st = rest.getForEntity(URL + req.getRequestURI(), Integer.class);
		Integer count = st.getBody();
		return count;
	}

	@Transactional
	@RequestMapping(value = { "/Borrower/Branches/{id}/bookCounts",
			"Librarian/Branches/{id}/bookCount" }, method = RequestMethod.GET, produces = { "application/json",
					"application/xml" })
	public Integer getCountByPK(@PathVariable Integer id, HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "" + id);
		ResponseEntity<Integer> st = rest.getForEntity(URL + req.getRequestURI(), Integer.class, params);
		Integer count = st.getBody();
		return count;
	}
	
	@Transactional
	@RequestMapping(value = {"/Borrower/Branches/{branchId}/CardNo/{cardNo}/bookLoansCount"}, method = RequestMethod.GET, produces = { "application/json",
					"application/xml" })
	public Integer getCountByPKs(@PathVariable Integer branchId, @PathVariable Integer cardNo, HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("cardNo", "" + cardNo);
		ResponseEntity<Integer> st = rest.getForEntity(URL + req.getRequestURI(), Integer.class, params);
		Integer count = st.getBody();
		return count;
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Authors/Name", "/Admin/Books/Name", "/Admin/Genres/Name",
			"/Admin/Publishers/Name", "/Admin/Branches/Name", "/Admin/Borrowers/Name", "/Admin/BookLoans/Name",
			"Librarian/Branches/Name", "/Borrower/Branches/Name/" }, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public List<Object> readByPage(@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "pageNo", required = false) Integer pageNo, HttpServletRequest req)
			throws SQLException {
		getURL(req);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL + req.getRequestURI())
				.queryParam("searchString", searchString).queryParam("pageNo", pageNo);
		ResponseEntity<List> st = rest.getForEntity(builder.toUriString(), List.class);
		List<Object> obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Authors/authorId/{id}", "/Admin/Books/bookId/{id}", "/Admin/Genres/genreId/{id}",
			"/Admin/Publishers/publisherId/{id}", "/Admin/Branches/branchId/{id}", "/Admin/Borrowers/cardNo/{id}",
			"Librarian/Books/bookId/{id}", "Librarian/Branches/branchId/{id}",
			"/Borrower/CardNo/{cardNo}" }, method = RequestMethod.GET, produces = { "application/json",
					"application/xml" })
	public Object readByPk(@PathVariable Integer id, HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "" + id);
		ResponseEntity<Object> st = rest.getForEntity(URL + req.getRequestURI(), Object.class, params);
		Object obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = { "/Admin/Branches/{branchId}/Books/{bookId}/CardNo/{cardNo}/BookLoan",
			"/Borrower/Branches/{branchId}/Books/{bookId}/CardNo/{cardNo}/BookLoan" }, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public Object readLoansByPk(@PathVariable Integer branchId, @PathVariable Integer bookId,
			@PathVariable Integer cardNo, HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		params.put("cardNo", "" + cardNo);
		ResponseEntity<Object> st = rest.getForEntity(URL + req.getRequestURI(), Object.class, params);
		Object obj = st.getBody();
		return obj;
	}

	@Transactional
	@RequestMapping(value = {
			"/Admin/Branches/{branchId}/Books/{bookId}/CardNo/{cardNo}/BookLoan" }, method = RequestMethod.DELETE, produces = {
					"application/json", "application/xml" })
	public void deleteLoans(@PathVariable Integer branchId, @PathVariable Integer bookId, @PathVariable Integer cardNo,
			HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		params.put("cardNo", "" + cardNo);
		rest.delete(URL + req.getRequestURI(), params);
	}

	@Transactional
	@RequestMapping(value = {
			"/Borrower/Branches/{branchId}/Books/{bookId}/CardNo/{cardNo}/borrowBook" }, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public void borrowBook(@PathVariable Integer branchId, @PathVariable Integer bookId, @PathVariable Integer cardNo,
			HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		params.put("cardNo", "" + cardNo);
		rest.delete(URL + req.getRequestURI(), params);
	}

	@Transactional
	@RequestMapping(value = {
			"/Borrower/Branches/{branchId}/Books/{bookId}/CardNo/{cardNo}/returnBook" }, method = RequestMethod.GET, produces = {
					"application/json", "application/xml" })
	public void returnBook(@PathVariable Integer branchId, @PathVariable Integer bookId, @PathVariable Integer cardNo,
			HttpServletRequest req) throws SQLException {
		getURL(req);
		Map<String, String> params = new HashMap<String, String>();
		params.put("branchId", "" + branchId);
		params.put("bookId", "" + bookId);
		params.put("cardNo", "" + cardNo);
		rest.delete(URL + req.getRequestURI(), params);
	}

	private void getURL(HttpServletRequest req) {
		if (req.getRequestURI().charAt(1) == 'A')
			URL = adminURL;
		else if (req.getRequestURI().charAt(1) == 'L')
			URL = librarianURL;
		else
			URL = borrowerURL;
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		int timeout = 5000;
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(timeout);
		return clientHttpRequestFactory;
	}

}
